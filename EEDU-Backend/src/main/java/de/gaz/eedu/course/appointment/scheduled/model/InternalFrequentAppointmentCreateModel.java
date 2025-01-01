package de.gaz.eedu.course.appointment.scheduled.model;

import de.gaz.eedu.course.appointment.scheduled.ScheduledAppointmentEntity;
import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.exception.CreationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;

public record InternalFrequentAppointmentCreateModel(long courseId, ScheduledAppointmentCreateModel data) implements CreationModel<ScheduledAppointmentEntity>
{
    @Override public @NotNull ScheduledAppointmentEntity toEntity(@NotNull ScheduledAppointmentEntity entity)
    {
        return data.toEntity(entity);
    }
}
