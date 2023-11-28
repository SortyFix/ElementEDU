package de.gaz.eedu.user.verfication.twofa.implementations;

import de.gaz.eedu.user.verfication.twofa.implementations.totp.HashingAlgorithm;
import de.gaz.eedu.user.verfication.twofa.implementations.totp.TOPTHandler;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter public enum TwoFactorMethod {
    EMAIL(new EmailImplementation()),
    SMS(new SMSImplementation()),
    TOTP(new TOTPImplementation(new TOPTHandler(HashingAlgorithm.SHA1)));

    private final TwoFactorMethodImplementation twoFactorMethodImplementation;

    TwoFactorMethod(@NotNull TwoFactorMethodImplementation twoFactorMethodImplementation)
    {
        this.twoFactorMethodImplementation = twoFactorMethodImplementation;
    }
}
