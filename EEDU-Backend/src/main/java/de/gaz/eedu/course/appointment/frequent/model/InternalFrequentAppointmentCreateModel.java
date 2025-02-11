package de.gaz.eedu.course.appointment.frequent.model;

import de.gaz.eedu.course.appointment.frequent.FrequentAppointmentEntity;
import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;

public record InternalFrequentAppointmentCreateModel(long courseId, FrequentAppointmentCreateModel data) implements CreationModel<FrequentAppointmentEntity>
{
    @Override public @NotNull FrequentAppointmentEntity toEntity(@NotNull FrequentAppointmentEntity entity)
    {
        return data.toEntity(entity);
    }
}
