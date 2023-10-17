package de.gaz.eedu.user.model;

import de.gaz.eedu.entity.model.Model;
import org.jetbrains.annotations.NotNull;

public record UserLoginModel(@NotNull String loginName, @NotNull String password) implements Model
{
}
