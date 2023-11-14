package de.gaz.eedu.user.verfication.model;

import de.gaz.eedu.user.model.UserModel;
import org.jetbrains.annotations.NotNull;

public record LoginSuccessResponse(@NotNull UserModel userModel, @NotNull String jwtToken) implements LoginResponse {}
