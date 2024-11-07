package de.gaz.eedu.user.verification;

import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.model.LoginModel;
import de.gaz.eedu.user.verification.authority.AuthorityFactory;
import de.gaz.eedu.user.verification.authority.VerificationAuthority;
import de.gaz.eedu.user.verification.credentials.CredentialEntity;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import de.gaz.eedu.user.verification.model.AdvancedUserLoginModel;
import de.gaz.eedu.user.verification.model.UserLoginModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAmount;
import java.util.*;

/**
 * The VerificationService is responsible for handling operations related to user verification and authorization.
 * <p>
 * This includes tasks such as generating JWT tokens, validating tokens and implementing two-factor authentication
 * mechanisms. It provides methods to generate login tokens for users with support for advanced features like
 * two-factor authentication. Each of these methods ensures secure and accurate operations by incorporating measures
 * like assigning unique user IDs and setting expiration limits on tokens.
 * <p>
 * This class is annotated with the @Service annotation, denoting that it's a service component in the Spring context
 * . The Spring framework will detect and register this class for dependency injection, where it can be autowired
 * into other components in the application.
 *
 * @author ivo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService
{
    @Value("${jwt.secret}") private String secret;

    /**
     * Checks for a specific token type.
     * <p>
     * This token checks whether the current {@link Authentication} is authenticated with a specific {@link JwtTokenType}.
     * It does so by iterating over the authorities and checking for {@link VerificationAuthority} objects of that
     * token type.
     * <p>
     * Users will always be granted a token with a {@link JwtTokenType} attached.
     * <p>
     * This method can be used within {@link org.springframework.security.access.prepost.PreAuthorize} by referencing this service
     *
     * <pre>
     * {@code
     *     @PreAuthorize("@verificationService.hasToken(T(de.gaz.eedu.user.verification.JwtTokenType).AUTHORIZED)")
     *     @GetMapping("/secretdata") public @NotNull ResponseEntity<String> getData()
     *     {
     *         return "Secret Data!";
     *     }
     * }
     * </pre>
     *
     * @param jwtTokenType the token to check whether the user is authenticated with it.
     * @return whether the token is present or not.
     *
     * @see #hasToken(Authentication, JwtTokenType...)
     */
    public boolean hasToken(@NotNull JwtTokenType... jwtTokenType)
    {
        return hasToken(SecurityContextHolder.getContext().getAuthentication(), jwtTokenType);
    }

    /**
     * Checks for a specific token type.
     * <p>
     * This token checks whether the current {@link Authentication} is authenticated with a specific {@link JwtTokenType}.
     * It does so by iterating over the authorities and checking for {@link VerificationAuthority} objects of that
     * token type.
     * <p>
     * Users will always be granted a token with a {@link JwtTokenType} attached.
     *
     * @param authentication the authentication context to check for.
     * @param jwtTokenType the token to check whether the user is authenticated with it.
     * @return whether the token is present or not.
     *
     * @see #hasToken(JwtTokenType...)
     */
    public boolean hasToken(@NotNull Authentication authentication, @NotNull JwtTokenType... jwtTokenType)
    {
        List<JwtTokenType> tokenTypes = Arrays.asList(jwtTokenType);
        return authentication.getAuthorities().stream().anyMatch(verification ->
        {
            if (verification instanceof VerificationAuthority verificationAuthority)
            {
                return tokenTypes.contains(verificationAuthority.jwtTokenType());
            }
            return false;
        });
    }

    /**
     * Generates a login JWT token for a user based on their user model and login model.
     * <p>
     * This method generates a JWT token for a user that is logging in. The process varies based on whether the user
     * has set up two-factor authentication or not and whether the login model is of type `AdvancedUserLoginModel`.
     * In case where two-factor authentication is enabled, the `twoFactor` method is used to generate the token,
     * otherwise the `authorizeToken` method is used.
     *
     * @param user  the user's account information
     * @param model the login credentials provided by the user
     * @return a JWT token in string format for the provided user
     * @throws NullPointerException     if `user` or `model` is null
     * @throws IllegalArgumentException if user's ID is not valid or `user.twoFactor()` returns invalid TwoFactorModels
     * @throws IllegalStateException    when there is an issue in JWT generation related to key generation or
     *                                  compacting the token
     */
    public @NotNull GeneratedToken requestLogin(@NotNull UserEntity user, @NotNull LoginModel model)
    {
/*        if (user.getCredentials().isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }*/

        Set<String> restricted = Set.of("expiry", "username");
        ClaimHolder<Long> expiry = new ClaimHolder<>("expiry", getExpiry(model).toEpochMilli());
        ClaimHolder<String> username = new ClaimHolder<>("username", user.getUsername());
        TokenData tokenData = new TokenData(user.getId(), model instanceof AdvancedUserLoginModel, restricted, expiry, username);

        if (user.getCredentials().isEmpty())
        {
            CredentialMethod[] methods = {CredentialMethod.PASSWORD};

            //noinspection ConstantValue
            JwtTokenType type = methods.length == 1 ? JwtTokenType.CREDENTIAL_CREATION_PENDING : JwtTokenType.CREDENTIAL_REQUIRED;
            return credentialToken(type, tokenData, methods);
        }

        CredentialMethod[] methods = getMethods(user);

        JwtTokenType type = methods.length == 1 ? JwtTokenType.CREDENTIAL_PENDING : JwtTokenType.CREDENTIAL_SELECTION;
        return credentialToken(type, tokenData, methods);
    }

    /**
     * Retrieves the distinct enabled two-factor authentication methods set by a user.
     * <p>
     * This private method retrieves enabled two-factor authentication methods from the user's two-factor settings.
     * Only unique methods are returned.
     *
     * @param user the user whose two-factor methods are to be retrieved
     * @return an array of distinct enabled two-factor methods
     * @throws NullPointerException if `user` is null or if `user.twoFactor()` returns null
     */
    private @NotNull CredentialMethod @NotNull [] getMethods(@NotNull UserEntity user)
    {
        Collection<CredentialEntity> credentialEntities = user.getCredentials().stream().filter(CredentialEntity::isEnabled).toList();
        if (credentialEntities.stream().allMatch(CredentialEntity::isTemporary))
        {
            return credentialEntities.stream().map(CredentialEntity::getMethod).toArray(CredentialMethod[]::new);
        }
        return credentialEntities.stream().filter(credentialEntity -> !credentialEntity.isTemporary()).map(
                CredentialEntity::getMethod).toArray(CredentialMethod[]::new);
    }

    public @NotNull GeneratedToken credentialToken(@NotNull JwtTokenType type, @NotNull TokenData tokenData, @NotNull CredentialMethod @NotNull ... credentialMethod)
    {
        switch (type)
        {
            case CREDENTIAL_PENDING, CREDENTIAL_REQUIRED, CREDENTIAL_CREATION_PENDING, CREDENTIAL_SELECTION ->
            {
                tokenData.addClaim(true, "available", credentialMethod);
                return generateKey(type, getExpiry(Duration.ofMinutes(5)), tokenData);
            }
            default -> throw new IllegalArgumentException("Unsupported token type");
        }
    }

    public @NotNull GeneratedToken authorizeToken(@NotNull TokenData tokenData)
    {
        JwtTokenType type = tokenData.advanced() ? JwtTokenType.ADVANCED_AUTHORIZATION : JwtTokenType.AUTHORIZED;
        Instant expiry = Instant.ofEpochMilli(tokenData.get("expiry", Long.class));

        tokenData.deleteRestrictedClaim("expiry");
        tokenData.deleteRestrictedClaim("available");
        tokenData.deleteRestrictedClaim("temporary");

        return generateKey(type, expiry, tokenData);
    }

    /**
     * Determines the expiration instant of a login session based on the provided LoginModel.
     * <p>
     * The method generates an expiration instant based on the type of LoginModel. The default expiration is after 1
     * day. If the LoginModel is an instance of UserLoginModel and its keepLoggedIn property is true, the expiration
     * is extended to 14 days.
     *
     * @param loginModel the LoginModel based on which the expiration instant is determined
     * @return the instant indicating when the login session expires
     * @throws NullPointerException if loginModel is null
     */
    private @NotNull Instant getExpiry(@NotNull LoginModel loginModel)
    {
        TemporalAmount temporalAmount = Duration.ofDays(1);
        if (loginModel instanceof UserLoginModel userLoginModel && userLoginModel.keepLoggedIn())
        {
            temporalAmount = Period.ofDays(14);
        }

        return getExpiry(temporalAmount);
    }

    /**
     * Calculates an expiration instant based on a given temporal amount added to the current system time.
     * <p>
     * The method adds the provided TemporalAmount to the current system time to calculate an expiration instant. The
     * current system time is obtained in the system's default timezone.
     *
     * @param temporalAmount the temporal amount to be added to the current system time
     * @return the instant time after adding the provided temporal amount to current system time
     * @throws NullPointerException if temporalAmount is null
     * @throws DateTimeException    if temporalAmount exceeds the capacity of Instant (something like 292 million
     *                              years lol)
     */
    private @NotNull Instant getExpiry(@NotNull TemporalAmount temporalAmount)
    {
        return Instant.now().atZone(ZoneId.systemDefault()).plus(temporalAmount).toInstant();
    }

    private @NotNull GeneratedToken generateKey(@NotNull JwtTokenType type, @NotNull Instant expiry, @NotNull TokenData tokenData)
    {
        return GeneratedToken.toToken(secret, type, expiry, tokenData);
    }

    /**
     * Validates the JWT token using the provided authority factory and returns a UsernamePasswordAuthenticationToken
     * if valid.
     * <p>
     * The method first parses the JWT token to extract its content, i.e., claims. It then uses these claims to
     * identify the user and determine their authorities. If token is valid, it returns an Optional
     * UsernamePasswordAuthenticationToken representing the authenticated user. This token contains the user's ID,
     * authorities, and the original token claims.
     *
     * @param token            JWT token to be validated
     * @param authorityFactory factory to generate authorities from JWT token and user ID
     * @return UsernamePasswordAuthenticationToken wrapped in an Optional if validation is successful, empty Optional
     * otherwise.
     * @throws UnsupportedJwtException  if argument passed is not a JWT token
     * @throws MalformedJwtException    if JWT token is not correctly structured
     * @throws SignatureException       if JWT token's signature does not match computed signature
     * @throws IllegalArgumentException if JWT token's compact value is null or empty, JWT claims string is null or
     *                                  empty, JWT claims map is empty, or any required claim is missing
     */
    public @NotNull Optional<UsernamePasswordAuthenticationToken> validate(@NotNull String token, @NotNull AuthorityFactory authorityFactory) throws ExpiredJwtException
    {
        TokenData data = TokenData.deserialize(secret, token);

        String jwtTokenTypeName = data.getParent().map(Claims::getSubject).orElseThrow();
        JwtTokenType type = JwtTokenType.valueOf(jwtTokenTypeName);

        Collection<? extends GrantedAuthority> authorities = getAuthorities(type, data.userId(), authorityFactory);
        return Optional.of(new UsernamePasswordAuthenticationToken(data.userId(), null, authorities)).map((auth) ->
        {
            auth.setDetails(data);
            return auth;
        });
    }

    private @Unmodifiable @NotNull Collection<? extends GrantedAuthority> getAuthorities(@NotNull JwtTokenType jwtTokenType, long userId, @NotNull AuthorityFactory factory)
    {
        return switch (jwtTokenType)
        {
            case ADVANCED_AUTHORIZATION, AUTHORIZED ->
            {
                Collection<GrantedAuthority> authorities = new HashSet<>(factory.get(userId));
                authorities.add(JwtTokenType.AUTHORIZED.getAuthority());
                if (jwtTokenType.equals(JwtTokenType.ADVANCED_AUTHORIZATION))
                {
                    authorities.add(JwtTokenType.ADVANCED_AUTHORIZATION.getAuthority());
                }
                yield authorities;
            }
            case CREDENTIAL_SELECTION, CREDENTIAL_PENDING, CREDENTIAL_REQUIRED, CREDENTIAL_CREATION_PENDING ->
                    Collections.singleton(jwtTokenType.getAuthority());
        };
    }
}
