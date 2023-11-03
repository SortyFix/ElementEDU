package de.gaz.eedu.user.model;


import de.gaz.eedu.entity.model.SimpleModel;
import de.gaz.eedu.user.theming.ThemeEntity;

public record SimpleUserModel(long id, String firstName, String lastName, String loginName,
                                           boolean enabled, Boolean locked, ThemeEntity theme) implements SimpleModel {}
