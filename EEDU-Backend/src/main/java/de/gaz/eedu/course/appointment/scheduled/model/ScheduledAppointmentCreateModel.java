package de.gaz.eedu.course.appointment.scheduled.model;

import de.gaz.eedu.course.appointment.scheduled.ScheduledAppointmentEntity;
import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.exception.CreationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;

public record ScheduledAppointmentCreateModel(@NotNull Long start, @NotNull Long until, @NotNull Long room, @NotNull Long duration, @NotNull Long frequency) implements CreationModel<ScheduledAppointmentEntity>
{
    @Override public @NotNull ScheduledAppointmentEntity toEntity(@NotNull ScheduledAppointmentEntity entity)
    {
        entity.setStartTimeStamp(Instant.ofEpochMilli(start()));
        entity.setUntilTimeStamp(Instant.ofEpochMilli(until()));
        entity.setDuration(Duration.ofMillis(duration()));
        entity.setFrequency(Period.ofDays(Math.toIntExact(computedFrequency())));
        return entity;
    }

    public @NotNull Long computedFrequency()
    {
        long days = (frequency() / 86400000);
        if (days < 1)
        {
            throw new CreationException(HttpStatus.BAD_REQUEST);
        }
        return days;
    }
}
