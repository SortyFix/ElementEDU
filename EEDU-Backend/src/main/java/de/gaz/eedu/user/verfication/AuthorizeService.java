package de.gaz.eedu.user.verfication;

import de.gaz.eedu.user.model.LoginModel;
import de.gaz.eedu.user.model.UserModel;
import de.gaz.eedu.user.verfication.authority.AuthorityFactory;
import de.gaz.eedu.user.verfication.authority.InvalidTokenException;
import de.gaz.eedu.user.verfication.twofa.implementations.TwoFactorMethod;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Getter(AccessLevel.PROTECTED) @Service public class AuthorizeService
{
    private final VerificationService verificationService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthorizeService(@Autowired VerificationService verificationService)
    {
        this.verificationService = verificationService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional public @Nullable String login(@NotNull UserModel model, @NotNull String hashedPassword, @NotNull LoginModel loginModel)
    {
        if (getPasswordEncoder().matches(loginModel.password(), hashedPassword))
        {
            return getVerificationService().loginUserToken(model, loginModel);
        }
        return null; //password does not match
    }

    public @NotNull String selectTwoFactor(@NotNull TwoFactorMethod twoFactorMethod, @NotNull Claims claims)
    {
        ClaimDecoder claimDecoder = ClaimDecoder.decode(claims);
        return getVerificationService().twoFactor(claimDecoder.userID(), claimDecoder.expiry(), claimDecoder.advanced(), twoFactorMethod);
    }

    @Transactional public @Nullable String authorize(@NotNull UserModel userModel, @NotNull Claims claims)
    {
        ClaimDecoder claimDecoder = ClaimDecoder.decode(claims);

        if(claimDecoder.userID() != userModel.id())
        {
            return null;
        }

        return getVerificationService().authorizeToken(userModel, claimDecoder.expiry(), claimDecoder.advanced());
    }

    public @NotNull Optional<UsernamePasswordAuthenticationToken> validate(@NotNull String token, @NotNull AuthorityFactory authorityFactory)
    {
        return getVerificationService().validate(token, authorityFactory);
    }

    public @NotNull String encode(@NotNull String password)
    {
        return getPasswordEncoder().encode(password);
    }

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
