package de.gaz.eedu.user.verfication.twofa.implementations;

import de.gaz.eedu.user.UserService;
import org.jetbrains.annotations.NotNull;

public interface TwoFactorMethodImplementation
{

    @NotNull <T> T setup(@NotNull UserService userService, @NotNull Long userID);

    boolean verify(@NotNull UserService userService, @NotNull Long userID, String code);

}
