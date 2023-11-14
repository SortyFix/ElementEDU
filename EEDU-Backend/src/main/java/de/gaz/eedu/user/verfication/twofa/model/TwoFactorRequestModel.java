package de.gaz.eedu.user.verfication.twofa.model;

import de.gaz.eedu.entity.model.Model;
import de.gaz.eedu.user.verfication.twofa.TwoFactorMethod;
import org.jetbrains.annotations.NotNull;

public record TwoFactorRequestModel(@NotNull Long userId, @NotNull TwoFactorMethod twoFactorMethod) implements Model
{
}
