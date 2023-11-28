package de.gaz.eedu.user.verfication.model;

import de.gaz.eedu.user.verfication.twofa.implementations.TwoFactorMethod;
import org.jetbrains.annotations.NotNull;

public record LoginTwoFactorRequiredResponse(@NotNull TwoFactorMethod[] possible, @NotNull String twoFactor) implements LoginResponse {}
