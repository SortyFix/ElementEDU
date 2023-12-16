package de.gaz.eedu.user.group.model;

import de.gaz.eedu.entity.model.SimpleModel;
import de.gaz.eedu.user.privileges.model.SimplePrivilegeModel;
import jakarta.validation.constraints.NotNull;


public record SimpleUserGroupModel(long id, @NotNull String name,
                                   @NotNull SimplePrivilegeModel[] privileges) implements SimpleModel {
}
