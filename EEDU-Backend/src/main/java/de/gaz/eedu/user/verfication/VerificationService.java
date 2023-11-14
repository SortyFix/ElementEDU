package de.gaz.eedu.user.verfication;

import de.gaz.eedu.user.model.UserModel;
import de.gaz.eedu.user.verfication.model.*;
import de.gaz.eedu.user.verfication.twofa.TwoFactorMethod;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

@Service public class VerificationService
{
    @Value("${jwt.secret}") private String secret;

    public @NotNull BCryptPasswordEncoder getEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    public @NotNull LoginResponseModel twoFactor(@NotNull UserModel userModel, @NotNull Instant expiry)
    {
        ZoneId zid = ZoneId.systemDefault();
        Instant fiveMinutes = Instant.now().atZone(zid).plusMinutes(5).toInstant();
        ClaimHolder<?>[] claimHolders = new ClaimHolder[]{userID(userModel.id()), new ClaimHolder<>("timeStamp",
                expiry)};
        //TODO add two factor to user model
        //TwoFactorMethod[] twoFactorMethods = userModel.getEnabledTwoFactorMethods().stream().map
        // (TwoFactorEntity::getMethod).toList().toArray(new TwoFactorMethod[0]);
        String twoFAJwtToken = generateKey(JwtTokenType.TWO_FACTOR.name(), fiveMinutes, claimHolders);
        return new LoginResponseModel(new LoginTwoFactorResponse(new TwoFactorMethod[0], twoFAJwtToken));
    }

    public @NotNull LoginResponseModel twoFactorRequired(@NotNull UserModel userModel, @NotNull Instant expiry)
    {
        String loginToken = generateKey(JwtTokenType.TWO_FACTOR_PENDING.name(), expiry, userID(userModel.id()));
        return new LoginResponseModel(new LoginTwoFactorRequiredResponse(userModel.id(), loginToken));
    }

    public @NotNull LoginResponseModel loginSuccess(@NotNull UserModel userModel, @NotNull Instant expiry)
    {
        String loginToken = generateKey(JwtTokenType.AUTHORIZED.name(), expiry, userID(userModel.id()));
        return new LoginResponseModel(new LoginSuccessResponse(userModel, loginToken));
    }

    private @NotNull String generateKey(@NotNull String subject, @NotNull Instant expires,
            @NotNull ClaimHolder<?>... claims)
    {
        Map<String, Object> jwtClaims = new HashMap<>();
        Arrays.stream(claims).forEach(claim -> jwtClaims.put(claim.key(), claim.content()));

        return Jwts.builder().subject(subject).claims(jwtClaims).expiration(Date.from(expires)).issuedAt(new Date()).signWith(getKey()).compact();
    }

    public @NotNull Optional<UsernamePasswordAuthenticationToken> validate(@NotNull String token,
            @NotNull AuthorityFactory authorityFactory)
    {
        Claims tokenContent = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
        long userID = tokenContent.get("userID", Long.class);

        return Optional.of(new UsernamePasswordAuthenticationToken(userID, null,
                switch (JwtTokenType.valueOf(tokenContent.getSubject()))
        {
            case AUTHORIZED -> authorityFactory.get(userID);
            case ADVANCED_AUTHORIZATION ->
            {
                Collection<GrantedAuthority> authorities = new HashSet<>(authorityFactory.get(userID));
                authorities.add(JwtTokenType.ADVANCED_AUTHORIZATION.getAuthority());
                yield authorities;
            }
            case TWO_FACTOR_PENDING -> Collections.singleton(JwtTokenType.TWO_FACTOR_PENDING.getAuthority());
            case TWO_FACTOR -> Collections.singleton(JwtTokenType.TWO_FACTOR.getAuthority());
            case TWO_FACTOR_CREATE -> Collections.singleton(JwtTokenType.TWO_FACTOR_CREATE.getAuthority());
        }));
    }

    private @NotNull SecretKey getKey()
    {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Contract("_ -> new") private @NotNull ClaimHolder<Long> userID(long userID)
    {
        return new ClaimHolder<>("userID", userID);
    }
}
