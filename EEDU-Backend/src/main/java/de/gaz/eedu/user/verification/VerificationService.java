package de.gaz.eedu.user.verification;

import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.model.LoginModel;
import de.gaz.eedu.user.verification.authority.AuthorityFactory;
import de.gaz.eedu.user.verification.authority.InvalidTokenException;
import de.gaz.eedu.user.verification.authority.VerificationAuthority;
import de.gaz.eedu.user.verification.credentials.CredentialController;
import de.gaz.eedu.user.verification.credentials.CredentialEntity;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import de.gaz.eedu.user.verification.credentials.model.CredentialCreateModel;
import de.gaz.eedu.user.verification.model.AdvancedUserLoginModel;
import de.gaz.eedu.user.verification.model.UserLoginModel;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.stream.Stream;

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
@Service
public class VerificationService
{
    @Value("${jwt.secret}") private String secret;

    /**
     * Generates a login JWT token for a user based on their user model and login model.
     * <p>
     * This method generates a JWT token for a user that is logging in. The process varies based on whether the user
     * has set up two-factor authentication or not and whether the login model is of type `AdvancedUserLoginModel`.
     * In case where two-factor authentication is enabled, the `twoFactor` method is used to generate the token,
     * otherwise the `authorizeToken` method is used.
     *
     * @param user       the user's account information
     * @param loginModel the login credentials provided by the user
     * @return a JWT token in string format for the provided user
     * @throws NullPointerException     if `user` or `loginModel` is null
     * @throws IllegalArgumentException if user's ID is not valid or `user.twoFactor()` returns invalid TwoFactorModels
     * @throws IllegalStateException    when there is an issue in JWT generation related to key generation or
     *                                  compacting the token
     */
    public @NotNull String requestLogin(@NotNull UserEntity user, @NotNull LoginModel loginModel)
    {
        boolean advanced = loginModel instanceof AdvancedUserLoginModel;
        Instant expiry = getExpiry(loginModel);

/*        if (user.getCredentials().isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }*/


        if (user.getCredentials().isEmpty())
        {
            return credentialRequired(
                    user.getId(),
                    null,
                    expiry,
                    advanced,
                    CredentialMethod.TOTP,
                    CredentialMethod.PASSWORD,
                    CredentialMethod.EMAIL);
        }

        return credentialToken(user.getId(), expiry, advanced, getMethods(user));
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

    public boolean hasToken(@NotNull JwtTokenType... jwtTokenType)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<JwtTokenType> tokenTypes = Set.of(jwtTokenType);
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
     * Generates a two-factor authentication JWT token for a user.
     * <p>
     * This method generates a JWT token for a user that is going through a two-factor authentication process. The
     * token contains various claims such as user ID, expiry, whether it's an advanced token, and the two-factor
     * method(s) that will be used. The type of JWT token varies based on the number of two-factor methods provided.
     *
     * @param userID           the user's ID
     * @param expiry           the expiration time of the token
     * @param advanced         a flag indicating whether it's an advanced token
     * @param credentialMethod the method(s) used for two-factor authentication
     * @return a JWT token in string format
     * @throws NullPointerException     if `expiry` or `twoFactorMethod` is null or if `twoFactorMethod` has null
     *                                  elements
     * @throws IllegalArgumentException if `twoFactorMethod` is empty
     * @throws IllegalStateException    if there is an issue with JWT generation related to key generation or
     *                                  compacting the token
     */
    public @NotNull String credentialToken(long userID, @NotNull Instant expiry, boolean advanced, @NotNull CredentialMethod @NotNull ... credentialMethod)
    {
        KeyType keyType = getKeyType(credentialMethod);

        ClaimHolder<?>[] holders = {new ClaimHolder<>("userID", userID), new ClaimHolder<>(
                "expiry",
                expiry.toEpochMilli()), new ClaimHolder<>("advanced", advanced), keyType.method()};

        return generateKey(keyType.type(), getExpiry(Duration.ofMinutes(5)), holders);
    }

    /**
     * Generates a token for the user to be able to set up two-factor.
     * <p>
     * This method generates a token for making a user able to set up two-factor. It is required when a group enforces
     * two-factor on their members but this user does not have two-factor setup yet.
     * <p>
     * This token enables them to use:
     * <ul>
     *     <li>{@link CredentialController#create(CredentialCreateModel)}</li>
     * </ul>
     * <p>
     * Note that the user can still decide themselves what {@link CredentialMethod} they set up.
     *
     * @param userID           id of the user.
     * @param temporary        TODO
     * @param expiry           when the login token should expire after the two-factor has been set up.
     * @param advanced         whether this token should have advanced user rights.
     * @param credentialMethod TODO
     * @return the token which the user then can use to create and enable a {@link CredentialMethod}.
     */
    public @NotNull String credentialRequired(long userID, @Nullable Long temporary, @NotNull Instant expiry, boolean advanced, @NotNull CredentialMethod @NotNull ... credentialMethod)
    {
        Instant time = getExpiry(Duration.of(5, ChronoUnit.MINUTES));

        ClaimHolder<?> temporaryIdClaim = null;
        if (temporary != null)
        {
            temporaryIdClaim = new ClaimHolder<>("temporaryId", temporary);
        }

        ClaimHolder<?>[] claimHolders = Stream.of(new ClaimHolder<>("userID", userID),
                new ClaimHolder<>("expiry", expiry.toEpochMilli()),
                new ClaimHolder<>("advanced", advanced),
                new ClaimHolder<>("available", credentialMethod),
                temporaryIdClaim).filter(Objects::nonNull).toArray(ClaimHolder<?>[]::new);

        JwtTokenType jwtTokenType = JwtTokenType.CREDENTIAL_REQUIRED;
        if(credentialMethod.length == 1)
        {
            jwtTokenType = JwtTokenType.CREDENTIAL_CREATION_PENDING;
        }

        return generateKey(jwtTokenType, time, claimHolders);
    }

    /**
     * Determines the key type used for two-factor authentication based on the given two-factor methods.
     * <p>
     * This method creates a KeyType instance that holds the respective JwtTokenType and the method(s) used for
     * two-factor authentication. The JwtTokenType and method are determined based on the number of two-factor
     * methods provided.
     *
     * @param credentialMethod the two-factor methods used for authentication
     * @return a KeyType instance containing the JwtTokenType and the method(s) used for two-factor authentication
     * @throws NullPointerException     if `twoFactorMethod` is null or if `twoFactorMethod` has null elements
     * @throws IllegalArgumentException if `twoFactorMethod` is empty
     */
    private @NotNull VerificationService.KeyType getKeyType(@NotNull CredentialMethod @NotNull [] credentialMethod)
    {
        boolean single = credentialMethod.length == 1;

        ClaimHolder<?> method = new ClaimHolder<>("available", credentialMethod);
        JwtTokenType type = JwtTokenType.CREDENTIAL_SELECTION;

        if (single)
        {
            method = new ClaimHolder<>("available", credentialMethod); // keep array format (singleton)
            type = JwtTokenType.CREDENTIAL_PENDING;
        }
        return new KeyType(method, type);
    }

    /**
     * Authorizes a user and provides an associated JWT token based on the user model and provided expiration instant.
     * <p>
     * The method authorizes the given UserModel and returns a JWT token string. The token type is set as AUTHORIZED
     * or ADVANCED_AUTHORIZATION based on the 'advanced' parameter. User's ID is included as a claim in the token.
     *
     * @param userID   the user to be authorized
     * @param expiry   the time at which the JWT token expires
     * @param advanced a flag indicating whether an advanced authorization token is to be generated
     * @return a JWT token in string format
     * @throws NullPointerException     if user is null, or expiry is null
     * @throws IllegalArgumentException if user's ID is invalid
     */
    public @NotNull String authorizeToken(long userID, @NotNull Instant expiry, boolean advanced)
    {
        JwtTokenType jwtTokenType = JwtTokenType.AUTHORIZED;
        if (advanced)
        {
            jwtTokenType = JwtTokenType.ADVANCED_AUTHORIZATION;
        }
        return generateKey(jwtTokenType, expiry, new ClaimHolder<>("userID", userID));
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

    /**
     * Generates a JWT token using the given token type, expiry date, and claim holders.
     * <p>
     * The method encapsulates the provided JwtTokenType, Instant expiry timestamp and ClaimHolder(s) into a JWT
     * token. It internally calls the overloaded version of this method with the JwtTokenType's name as the subject.
     *
     * @param jwtTokenType name of the JwtTokenType, which will be the subject of the JWT token
     * @param expiry       timestamp representing when the JWT token is set to expire
     * @param claims       variable arguments representing the claims to be put in the JWT token
     * @return a JWT token in string format
     * @throws NullPointerException     if jwtTokenType is null, or expiry is null, or claims are null
     * @throws IllegalArgumentException if JwtTokenType name does not comply with JWT subject rules
     * @throws IllegalStateException    if HMAC-SHA secret key generation errors out during the JWT creation.
     */
    private @NotNull String generateKey(@NotNull JwtTokenType jwtTokenType, @NotNull Instant expiry, @NotNull ClaimHolder<?>... claims)
    {
        return generateKey(jwtTokenType.name(), expiry, claims);
    }

    /**
     * Generates a JWT token using the given subject, expiry date, and claim holders.
     * <p>
     * The method takes a subject, an expiry date and N number of claims to create a JWT token. It puts all
     * ClaimHolders content into a Map, creates a new JWT with this map of claims, sets the provided subject and
     * expiry date, uses HMAC SHA Secret Key for signing, and finally returning the token.
     *
     * @param subject subject field for the JWT token. Typically identifying the principal that the JWT claims about.
     * @param expires timestamp representing when the JWT token is set to expire
     * @param claims  variable arguments representing the claims to be put into the JWT token
     * @return a JWT token in string format
     * @throws NullPointerException     if subject is null, or expires is null, or claims are null
     * @throws IllegalStateException    if HMAC-SHA secret key generation errors out during the JWT creation.
     * @throws IllegalArgumentException if provided subject does not comply with JWT subject rules
     */
    private @NotNull String generateKey(@NotNull String subject, @NotNull Instant expires, @NotNull ClaimHolder<?>... claims)
    {
        Map<String, Object> jwtClaims = new HashMap<>();
        Arrays.stream(claims).forEach(claim -> jwtClaims.put(claim.key(), claim.content()));

        return Jwts.builder().subject(subject).claims(jwtClaims).expiration(Date.from(expires)).issuedAt(new Date()).signWith(
                getKey()).compact();
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
        Claims tokenContent = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
        long userID = tokenContent.get("userID", Long.class);
        JwtTokenType jwtTokenType = JwtTokenType.valueOf(tokenContent.getSubject());

        Collection<? extends GrantedAuthority> authorities = getAuthorities(jwtTokenType, authorityFactory, userID);
        return Optional.of(new UsernamePasswordAuthenticationToken(userID, null, authorities)).map((auth) ->
        {
            auth.setDetails(tokenContent);
            return auth;
        });
    }

    /**
     * Generates a collection of authorities based on JwtTokenType, AuthorityFactory, and user ID.
     * <p>
     * The method determines the authorities of a user based on the type of JWT token, user ID and utilizes
     * AuthorityFactory. Depending on different JwtTokenTypes, different authorities are generated:
     * <ul>
     *   <li>ADVANCED_AUTHORIZATION, AUTHORIZED - create a new collection of authorities. For ADVANCED_AUTHORIZATION,
     *   add an additional authority</li>
     *   <li>TWO_FACTOR_SELECTION, TWO_FACTOR_PENDING - only single authority is generated</li>
     * </ul>
     *
     * @param jwtTokenType     the JwtTokenType identifying the type of JWT token
     * @param authorityFactory factory used to generate user authorities according to user ID
     * @param userID           the ID of the user whose authorities are to be generated
     * @return an unmodifiable collection of authorities, derived from jwtTokenType, authorityFactory and user ID
     * @throws IllegalStateException if the jwtTokenType does not match any case in the switch statement
     */
    @Unmodifiable @NotNull
    private Collection<? extends GrantedAuthority> getAuthorities(@NotNull JwtTokenType jwtTokenType, @NotNull AuthorityFactory authorityFactory, long userID)
    {
        return switch (jwtTokenType)
        {
            case ADVANCED_AUTHORIZATION, AUTHORIZED ->
            {
                Collection<GrantedAuthority> authorities = new HashSet<>(authorityFactory.get(userID));
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

    /**
     * Retrieves a SecretKey instance using the HMAC SHAKey algorithm.
     * <p>
     * The HMAC SHAKey algorithm is a type of Mac (Message Authentication Code) algorithm that combines the power of
     * Hash algorithms with the strength of AES (Advanced Encryption Standard). This method takes the predefined
     * 'secret' string value, converts it to a byte array using UTF-8 encoding, and then generates a HMAC SHAKey from
     * that byte array. The resulting SecretKey can be used for further cryptographic operations that require a
     * secret key, like creating or verifying a JWT (JSON Web Token).
     *
     * @return a SecretKey derived from the 'secret' string value using HMAC SHAKey algorithm and UTF-8 encoded bytes.
     * @throws IllegalStateException    if UTF-8 encoding is not supported or 'secret' string value cannot be
     *                                  converted to bytes.
     * @throws IllegalArgumentException if 'secret' string value is null or empty.
     */
    private @NotNull SecretKey getKey()
    {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public @NotNull String selectCredential(@NotNull CredentialMethod credentialMethod, @NotNull Claims claims)
    {
        ClaimDecoder claimDecoder = validate(credentialMethod.name(), claims);
        return credentialToken(claimDecoder.userID(), claimDecoder.expiry(), claimDecoder.advanced(), credentialMethod);
    }

    public @NotNull String requestSetupCredential(@Nullable Long temporary, @NotNull CredentialMethod credentialMethod, @NotNull Claims claims)
    {
        ClaimDecoder claimDecoder = validate(credentialMethod.name(), claims);
        long userID = claimDecoder.userID;
        return credentialRequired(userID, temporary, claimDecoder.expiry(), claimDecoder.advanced(), credentialMethod);
    }

    private @NotNull ClaimDecoder validate(@NotNull String credentialMethod, @NotNull Claims claims)
    {
        if (!((List<String>) claims.get("available")).contains(credentialMethod))
        {
            throw new InvalidTokenException();
        }

        return ClaimDecoder.decode(claims);
    }

    @Transactional public @Nullable String authorize(@NotNull Claims claims)
    {
        ClaimDecoder claimDecoder = ClaimDecoder.decode(claims);

        return authorizeToken(claimDecoder.userID(), claimDecoder.expiry(), claimDecoder.advanced());
    }

    /**
     * A private record class that helps with creating claims.
     * <p>
     * This private record is a helper class for creating Claims. These claims are saved in a
     * {@link Map} wit the types String and Object.
     * With this class multiple objects can be added to such a list as key and value are both included within this
     * object.
     *
     * @param key     the key of the claim.
     * @param content the content of the claim.
     * @param <T>     the type of the content.
     */
    private record ClaimHolder<T>(@NotNull String key, @NotNull T content)
    {}

    /**
     * A private record class that represents a particular JWT token type and its associated claim holder method.
     * <p>
     * This record class private inner class pairs a method for holding a ClaimHolder, and a JwtTokenType. The
     * ClaimHolder is a generic type that can hold any type of claim, i.e., it might be a claim about the user
     * identity, a claim about the user's authorization, etc. The JwtTokenType is an enumerated type that can hold
     * the type of JWT token, i.e., it might be an access token, refresh token, etc.
     *
     * @param method A particular method for holding a claim holder. It is wrapped in a ClaimHolder generic class.
     * @param type   An enumerated type representing the type of JWT token.
     */
    private record KeyType(ClaimHolder<?> method, JwtTokenType type)
    {}

    /**
     * A decoder for JWT tokens.
     * <p>
     * This record serves as a helper class for decoding jwt tokens send to the backend.
     * A jwt token must always contain the following attributes:
     * <ul>
     *     <li>userID</li>
     *     <li>expiry</li>
     *     <li>advanced</li>
     * </ul>
     *
     * @param userID   the id of the user.
     * @param expiry   the expiry of the token, or the token created with the current token.
     * @param advanced if the token has advanced access to user management areas.
     */
    private record ClaimDecoder(long userID, @NotNull Instant expiry, boolean advanced)
    {
        private static @NotNull ClaimDecoder decode(@NotNull Claims claims) throws InvalidTokenException
        {
            long userID = verify(claims.get("userID", Long.class));
            Instant expiry = Instant.ofEpochMilli(verify(claims.get("expiry", Long.class)));
            boolean advanced = verify(claims.get("advanced", Boolean.class));
            return new ClaimDecoder(userID, expiry, advanced);
        }

        private static <T> @NotNull T verify(@Nullable T value) throws InvalidTokenException
        {
            if (value == null)
            {
                throw new InvalidTokenException();
            }
            return value;
        }
    }
}
