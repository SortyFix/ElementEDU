package de.gaz.eedu.user.verfication.twofa.implementations;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter public enum TwoFactorMethod {
    EMAIL(new EmailImplementation()),
    SMS(new SMSImplementation()),
    TOTP(new TOTPImplementation());

    private final TwoFactorMethodImplementation twoFactorMethodImplementation;

    TwoFactorMethod(@NotNull TwoFactorMethodImplementation twoFactorMethodImplementation)
    {
        this.twoFactorMethodImplementation = twoFactorMethodImplementation;
    }
}
