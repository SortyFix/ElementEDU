package de.gaz.eedu.user.verfication.model;

import de.gaz.eedu.entity.model.Model;
import org.jetbrains.annotations.NotNull;

public record LoginResponseModel(@NotNull LoginResponse data) implements Model {}
