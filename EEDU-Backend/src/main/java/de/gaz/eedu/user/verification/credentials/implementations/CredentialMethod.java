package de.gaz.eedu.user.verification.credentials.implementations;

import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.verification.credentials.implementations.totp.HashingAlgorithm;
import de.gaz.eedu.user.verification.credentials.implementations.totp.TOPTHandler;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

@Getter public enum CredentialMethod {
    PASSWORD(new PasswordCredential(new BCryptPasswordEncoder())),
    EMAIL(new EmailCredential()),
    SMS(new SMSCredential()),
    TOTP(new TOTPCredential(new TOPTHandler(HashingAlgorithm.SHA1)));

    private final Credential credential;

    CredentialMethod(@NotNull Credential credential)
    {
        this.credential = credential;
    }

    public long toId(@NotNull UserEntity user)
    {
        return Objects.hash(user.getId(), ordinal());
    }
}
