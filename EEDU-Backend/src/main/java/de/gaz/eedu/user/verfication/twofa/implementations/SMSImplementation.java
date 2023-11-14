package de.gaz.eedu.user.verfication.twofa.implementations;

import de.gaz.eedu.user.UserService;
import org.jetbrains.annotations.NotNull;

public class SMSImplementation implements TwoFactorMethodImplementation
{
    @Override public @NotNull String setup(@NotNull UserService userService, @NotNull Long userID)
    {
        return null;
    }

    @Override public boolean verify(@NotNull UserService userService, @NotNull Long userID, String code)
    {
        return false;
    }
}
