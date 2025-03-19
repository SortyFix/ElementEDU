package de.gaz.eedu.course.appointment.entry.assignment.assessment.model;

import de.gaz.eedu.course.appointment.entry.assignment.assessment.AssessmentEntity;
import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record AssessmentCreateModel(long appointment, long user, @Nullable String feedback) implements CreationModel<Long, AssessmentEntity>
{
    @Override public @NotNull AssessmentEntity toEntity(@NotNull AssessmentEntity entity)
    {
        entity.setFeedback(feedback());
        return entity;
    }
}
