package de.gaz.eedu.user.group.model;

import de.gaz.eedu.entity.model.SimpleModel;
import de.gaz.eedu.user.model.SimpleUserModel;
import jakarta.validation.constraints.NotNull;

public record SimplePrivilegeGroupModel(long id, @NotNull String name, boolean requiresTwoFactor,
                                        @NotNull SimpleUserModel[] users) implements SimpleModel
{}
