package de.gaz.eedu.user.verification;

import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.model.LoginModel;
import de.gaz.eedu.user.verification.authority.AuthorityFactory;
import de.gaz.eedu.user.verification.authority.InvalidTokenException;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Getter(AccessLevel.PROTECTED) @RequiredArgsConstructor @Service public class AuthorizeService
{
    private final VerificationService verificationService;

    @Transactional public @Nullable String requestLogin(@NotNull UserEntity user, @NotNull LoginModel loginModel)
    {
        return getVerificationService().requestLogin(user, loginModel);
    }

    public @NotNull String selectCredential(@NotNull CredentialMethod credentialMethod, @NotNull Claims claims)
    {
        ClaimDecoder claimDecoder = validate(credentialMethod.name(), claims);
        return getVerificationService().credentialToken(claimDecoder.userID(),
                claimDecoder.expiry(),
                claimDecoder.advanced(),
                credentialMethod);
    }

    public @NotNull String requestSetupCredential(@NotNull CredentialMethod credentialMethod, @NotNull Claims claims)
    {
        ClaimDecoder claimDecoder = validate(credentialMethod.name(), claims);
        return getVerificationService().credentialRequired(claimDecoder.userID(),
                claimDecoder.expiry(),
                claimDecoder.advanced(),
                credentialMethod);
    }

    private @NotNull ClaimDecoder validate(@NotNull String credentialMethod, @NotNull Claims claims)
    {
        if (!((List<String>) claims.get("available")).contains(credentialMethod))
        {
            throw new InvalidTokenException();
        }

        return ClaimDecoder.decode(claims);
    }

    @Transactional public @Nullable String authorize(long userID, @NotNull Claims claims)
    {
        ClaimDecoder claimDecoder = ClaimDecoder.decode(claims);

        if (claimDecoder.userID() != userID)
        {
            return null;
        }

        return getVerificationService().authorizeToken(userID, claimDecoder.expiry(), claimDecoder.advanced());
    }

    public @NotNull Optional<UsernamePasswordAuthenticationToken> validate(
            @NotNull String token, @NotNull AuthorityFactory authorityFactory)
    {
        return getVerificationService().validate(token, authorityFactory);
    }

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
