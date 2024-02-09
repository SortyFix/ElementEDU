package de.gaz.eedu.course.classroom.model;

import de.gaz.eedu.course.classroom.ClassRoomEntity;
import de.gaz.eedu.entity.model.CreationModel;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public record ClassRoomCreateModel(@NonNull String name, @NotNull Long[] users, @NotNull Long[] courses) implements CreationModel<ClassRoomEntity>
{
    @Override
    public @NotNull ClassRoomEntity toEntity(@NotNull ClassRoomEntity entity)
    {
        entity.setName(name());
        return entity;
    }
}
