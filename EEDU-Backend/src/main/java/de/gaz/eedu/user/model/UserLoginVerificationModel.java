package de.gaz.eedu.user.model;

import org.jetbrains.annotations.NotNull;

public record UserLoginVerificationModel(long userID, @NotNull String jwtToken) {}
