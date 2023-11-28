package de.gaz.eedu.user.model;

import org.jetbrains.annotations.NotNull;

public record AdvancedUserLoginModel(@NotNull String loginName, @NotNull String password) implements LoginModel
{
}
