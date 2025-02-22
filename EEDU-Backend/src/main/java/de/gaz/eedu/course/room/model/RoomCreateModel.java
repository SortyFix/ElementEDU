package de.gaz.eedu.course.room.model;

import de.gaz.eedu.course.room.RoomEntity;
import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;

public record RoomCreateModel(@NotNull String name) implements CreationModel<Long, RoomEntity>
{
    @Override public @NotNull RoomEntity toEntity(@NotNull RoomEntity entity)
    {
        entity.setName(name());
        return entity;
    }
}
