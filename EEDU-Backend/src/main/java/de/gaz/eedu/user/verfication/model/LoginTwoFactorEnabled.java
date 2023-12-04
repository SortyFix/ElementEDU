package de.gaz.eedu.user.verfication.model;

import de.gaz.eedu.user.model.UserModel;
import org.jetbrains.annotations.NotNull;

public record LoginTwoFactorEnabled(@NotNull UserModel userModel) implements LoginResponse {}
