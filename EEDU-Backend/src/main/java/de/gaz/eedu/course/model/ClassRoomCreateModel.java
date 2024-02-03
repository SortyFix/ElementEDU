package de.gaz.eedu.course.model;

import de.gaz.eedu.course.ClassRoomEntity;
import de.gaz.eedu.entity.model.CreationModel;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public record ClassRoomCreateModel(@NonNull String name) implements CreationModel<ClassRoomEntity>
{
    @Override
    public @NotNull ClassRoomEntity toEntity(@NotNull ClassRoomEntity entity)
    {
        entity.setName(name());
        return entity;
    }
}
