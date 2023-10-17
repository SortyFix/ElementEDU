package de.gaz.eedu.user.model;

import de.gaz.eedu.entity.model.Model;
import org.jetbrains.annotations.NotNull;

public record UserLoginVerificationModel(long userID, @NotNull String jwtToken) implements Model {}
