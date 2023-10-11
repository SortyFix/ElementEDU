package de.gaz.eedu.user.model;

import org.jetbrains.annotations.NotNull;

public record UserLoginModel(@NotNull String loginName, @NotNull String password)
{
}
