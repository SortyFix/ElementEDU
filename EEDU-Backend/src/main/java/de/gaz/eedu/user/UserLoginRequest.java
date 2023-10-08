package de.gaz.eedu.user;

import org.jetbrains.annotations.NotNull;

public record UserLoginRequest(@NotNull String loginName, @NotNull String password)
{
}
