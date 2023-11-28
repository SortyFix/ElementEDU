package de.gaz.eedu.user.verfication.model;

import org.jetbrains.annotations.NotNull;

public record LoginTwoFactorPendingResponse(@NotNull String jwtToken) implements LoginResponse {}
