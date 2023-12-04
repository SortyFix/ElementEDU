package de.gaz.eedu.user.verfication.twofa.model;

import de.gaz.eedu.user.verfication.twofa.implementations.TwoFactorMethod;
import org.jetbrains.annotations.NotNull;

public record TwoFactorAuthModel(@NotNull String code, @NotNull TwoFactorMethod twoFactorMethod) {}
