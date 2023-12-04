package de.gaz.eedu.user.verfication.twofa.implementations;

import de.gaz.eedu.user.verfication.twofa.TwoFactorEntity;
import org.jetbrains.annotations.NotNull;

public interface TwoFactorMethodImplementation
{

    @NotNull <T> T creation(@NotNull TwoFactorEntity twoFactorEntity);

    boolean verify(@NotNull TwoFactorEntity twoFactorEntity, @NotNull String code);

    boolean enable(@NotNull TwoFactorEntity twoFactorEntity, @NotNull String code);

}
