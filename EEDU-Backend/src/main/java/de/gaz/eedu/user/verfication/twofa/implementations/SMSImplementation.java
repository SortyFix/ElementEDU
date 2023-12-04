package de.gaz.eedu.user.verfication.twofa.implementations;

import de.gaz.eedu.user.verfication.twofa.TwoFactorEntity;
import org.jetbrains.annotations.NotNull;

public class SMSImplementation implements TwoFactorMethodImplementation
{
    @Override public @NotNull String creation(@NotNull TwoFactorEntity twoFactorEntity)
    {
        return null;
    }

    @Override public boolean verify(@NotNull TwoFactorEntity twoFactorEntity, @NotNull String code)
    {
        return false;
    }

    @Override public boolean enable(@NotNull TwoFactorEntity twoFactorEntity, @NotNull String code)
    {
        return false;
    }
}
