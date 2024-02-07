package de.gaz.eedu.course.subjects.model;

import de.gaz.eedu.course.subjects.SubjectEntity;
import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;

public record SubjectCreateModel(@NotNull String name) implements CreationModel<SubjectEntity>
{
    @Override
    public @NotNull SubjectEntity toEntity(@NotNull SubjectEntity entity)
    {
        entity.setName(name);
        return entity;
    }
}
