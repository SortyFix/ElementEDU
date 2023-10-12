package de.gaz.eedu.user;

import org.jetbrains.annotations.NotNull;

public record UserLoginRequestModel(@NotNull String jwt, long id) {}
