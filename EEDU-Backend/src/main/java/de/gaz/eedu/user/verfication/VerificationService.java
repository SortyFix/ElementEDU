package de.gaz.eedu.user.verfication;

import de.gaz.eedu.user.model.AdvancedUserLoginModel;
import de.gaz.eedu.user.model.LoginModel;
import de.gaz.eedu.user.model.UserLoginModel;
import de.gaz.eedu.user.model.UserModel;
import de.gaz.eedu.user.verfication.authority.AuthorityFactory;
import de.gaz.eedu.user.verfication.model.*;
import de.gaz.eedu.user.verfication.twofa.implementations.TwoFactorMethod;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.function.Function;

@Service public class VerificationService
{
    @Value("${jwt.secret}") private String secret;

    public @NotNull LoginResponse loginUserToken(@NotNull UserModel user, @NotNull LoginModel loginModel)
    {
        boolean advanced = loginModel instanceof AdvancedUserLoginModel;
        Instant expiry = getExpiry(loginModel);

        TwoFactorMethod[] enabledMethods = getMethods(user);
        if (enabledMethods.length > 0)
        {
            if (enabledMethods.length == 1) // use if only one is registered
            {
                TwoFactorMethod method = enabledMethods[0];
                return twoFactor(user.id(), expiry, advanced, method);
            }
            return twoFactor(user.id(), expiry, advanced, enabledMethods);
        }

        return authorizeToken(user, expiry, advanced);
    }

    @NotNull private TwoFactorMethod @NotNull [] getMethods(@NotNull UserModel user)
    {
        return Arrays.stream(user.twoFactor())
                .filter(TwoFactorModel::enabled)
                .map(TwoFactorModel::method)
                .distinct()
                .toArray(TwoFactorMethod[]::new);
    }

    public @NotNull LoginTwoFactorPendingResponse twoFactor(long userID, @NotNull Instant expiry, boolean advanced,
            @NotNull TwoFactorMethod twoFactorMethod)
    {
        ClaimHolder<?>[] holders = {
                new ClaimHolder<>("userID", userID),
                new ClaimHolder<>("expiry", expiry.toEpochMilli()),
                new ClaimHolder<>("advanced", advanced),
                new ClaimHolder<>("method", twoFactorMethod)
        };

        return twoFactor(holders);
    }

    public @NotNull LoginTwoFactorRequiredResponse twoFactor(long userID, @NotNull Instant expiry, boolean advanced,
            @NotNull TwoFactorMethod[] twoFactorMethods)
    {
        ClaimHolder<?>[] holders = {
                new ClaimHolder<>("userID", userID),
                new ClaimHolder<>("expiry", expiry.toEpochMilli()),
                new ClaimHolder<>("advanced", advanced),
        };
        return twoFactor(twoFactorMethods, holders);
    }

    private @NotNull LoginTwoFactorPendingResponse twoFactor(@NotNull ClaimHolder<?>... claims)
    {
        String token = generateKey(JwtTokenType.TWO_FACTOR_PENDING, getExpiry(Duration.ofMinutes(5)), claims);
        return new LoginTwoFactorPendingResponse(token);
    }

    private @NotNull LoginTwoFactorRequiredResponse twoFactor(@NotNull TwoFactorMethod[] methods,
            @NotNull ClaimHolder<?>... claims)
    {
        String token = generateKey(JwtTokenType.TWO_FACTOR_SELECTION, getExpiry(Duration.ofMinutes(5)), claims);
        return new LoginTwoFactorRequiredResponse(methods, token);
    }

    public @NotNull LoginResponse authorizeToken(@NotNull UserModel userModel, @NotNull Instant expiry,
            boolean advanced)
    {
        JwtTokenType jwtTokenType = JwtTokenType.AUTHORIZED;
        if (advanced)
        {
            jwtTokenType = JwtTokenType.ADVANCED_AUTHORIZATION;
        }
        String token = generateKey(jwtTokenType, expiry, new ClaimHolder<>("userID", userModel.id()));
        return new LoginSuccessResponse(userModel, token);
    }

    private @NotNull Instant getExpiry(@NotNull LoginModel loginModel)
    {
        TemporalAmount temporalAmount = Duration.ofDays(1);
        if (loginModel instanceof UserLoginModel userLoginModel && userLoginModel.keepLoggedIn())
        {
            temporalAmount = Period.ofDays(14);
        }

        return getExpiry(temporalAmount);
    }

    private @NotNull Instant getExpiry(@NotNull TemporalAmount temporalAmount)
    {
        return Instant.now().atZone(ZoneId.systemDefault()).plus(temporalAmount).toInstant();
    }

    private @NotNull String generateKey(@NotNull JwtTokenType jwtTokenType, @NotNull Instant expiry,
            @NotNull ClaimHolder<?>... claims)
    {
        return generateKey(jwtTokenType.name(), expiry, claims);
    }

    private @NotNull String generateKey(@NotNull String subject, @NotNull Instant expires,
            @NotNull ClaimHolder<?>... claims)
    {
        Map<String, Object> jwtClaims = new HashMap<>();
        Arrays.stream(claims).forEach(claim -> jwtClaims.put(claim.key(), claim.content()));

        return Jwts.builder()
                .subject(subject)
                .claims(jwtClaims)
                .expiration(Date.from(expires))
                .issuedAt(new Date())
                .signWith(getKey())
                .compact();
    }

    @NotNull public Optional<UsernamePasswordAuthenticationToken> validate(@NotNull String token,
            @NotNull AuthorityFactory authorityFactory)
    {
        Claims tokenContent = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
        long userID = tokenContent.get("userID", Long.class);
        JwtTokenType jwtTokenType = JwtTokenType.valueOf(tokenContent.getSubject());

        Function<UsernamePasswordAuthenticationToken, UsernamePasswordAuthenticationToken> mapper = (auth) ->
        {
            auth.setDetails(tokenContent);
            return auth;
        };

        Collection<? extends GrantedAuthority> authorities = getAuthorities(jwtTokenType, authorityFactory, userID);
        return Optional.of(new UsernamePasswordAuthenticationToken(userID, null, authorities)).map(mapper);
    }

    @Unmodifiable @NotNull private Collection<? extends GrantedAuthority> getAuthorities(@NotNull JwtTokenType jwtTokenType, @NotNull AuthorityFactory authorityFactory, long userID)
    {
        return switch (jwtTokenType)
        {
            case ADVANCED_AUTHORIZATION, AUTHORIZED ->
            {
                Collection<GrantedAuthority> authorities = new HashSet<>(authorityFactory.get(userID));
                if (jwtTokenType.equals(JwtTokenType.ADVANCED_AUTHORIZATION))
                {
                    authorities.add(JwtTokenType.ADVANCED_AUTHORIZATION.getAuthority());
                }
                yield authorities;
            }
            case TWO_FACTOR_SELECTION, TWO_FACTOR_PENDING -> Collections.singleton(jwtTokenType.getAuthority());
        };
    }

    private @NotNull SecretKey getKey()
    {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
