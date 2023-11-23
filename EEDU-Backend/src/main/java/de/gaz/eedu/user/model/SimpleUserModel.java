package de.gaz.eedu.user.model;


import de.gaz.eedu.entity.model.SimpleModel;
import de.gaz.eedu.user.theming.SimpleThemeModel;
import jakarta.validation.constraints.NotNull;

public record SimpleUserModel(long id, @NotNull String firstName, @NotNull String lastName, @NotNull String loginName,
                              boolean enabled, Boolean locked, @NotNull SimpleThemeModel theme,
                              de.gaz.eedu.user.UserStatus status) implements SimpleModel {}
