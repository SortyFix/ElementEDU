package de.gaz.eedu.user.verfication.model;

import org.jetbrains.annotations.NotNull;

public record LoginTwoFactorRequiredResponse(@NotNull Long userID, String twoFactor) implements LoginResponse {}
