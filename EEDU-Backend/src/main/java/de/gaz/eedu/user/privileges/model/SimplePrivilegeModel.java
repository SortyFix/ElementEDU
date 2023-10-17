package de.gaz.eedu.user.privileges.model;

import de.gaz.eedu.entity.model.SimpleModel;
import jakarta.validation.constraints.NotNull;

public record SimplePrivilegeModel(@NotNull long id, @NotNull String name) implements SimpleModel {}
