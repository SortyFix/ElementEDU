package de.gaz.eedu.course.room.model;

import de.gaz.eedu.entity.model.EntityModel;
import org.jetbrains.annotations.NotNull;

public record RoomModel(@NotNull Long id, @NotNull String name) implements EntityModel
{
}
