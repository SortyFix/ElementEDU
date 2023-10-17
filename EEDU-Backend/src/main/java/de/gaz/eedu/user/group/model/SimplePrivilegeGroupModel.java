package de.gaz.eedu.user.group.model;

import de.gaz.eedu.entity.model.SimpleModel;
import de.gaz.eedu.user.model.SimpleUserModel;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record SimplePrivilegeGroupModel(long id, @NotNull String name,
                                        @NotNull Set<SimpleUserModel> users) implements SimpleModel
{}
