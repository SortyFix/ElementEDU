package de.gaz.eedu.user.verification.credentials.implementations;

import de.gaz.eedu.user.verification.credentials.implementations.totp.HashingAlgorithm;
import de.gaz.eedu.user.verification.credentials.implementations.totp.TOPTHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@AllArgsConstructor
@Getter public enum CredentialMethod {
    PASSWORD(new PasswordCredential(new BCryptPasswordEncoder()), false),
    EMAIL(new EmailCredential(), true),
    SMS(new SMSCredential(), true),
    TOTP(new TOTPCredential(new TOPTHandler(HashingAlgorithm.SHA1)), true);

    private final Credential credential;
    private final boolean enablingRequired;
}
