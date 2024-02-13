package de.gaz.eedu.course.model;

import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record CourseCreateModel(@NotNull String name, @NotNull Long subjectId, @Nullable Long classId, @NotNull Long[] users) implements CreationModel<CourseEntity>
{
    @Override
    public @NotNull CourseEntity toEntity(@NotNull CourseEntity entity)
    {
        entity.setName(name());
        return entity;
    }
}
