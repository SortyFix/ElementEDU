package de.gaz.eedu.course.appointment.scheduled.model;

import de.gaz.eedu.course.appointment.scheduled.ScheduledAppointmentEntity;
import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.exception.CreationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;

public record ScheduledAppointmentCreateModel(@NotNull Long start, @NotNull Long end, @NotNull Long duration, @NotNull Long period,
                                              @NotNull Long course) implements CreationModel<ScheduledAppointmentEntity>
{

    @Override public @NotNull ScheduledAppointmentEntity toEntity(@NotNull ScheduledAppointmentEntity entity)
    {
        entity.setStartTimeStamp(Instant.ofEpochSecond(start()));
        entity.setEndTimeStamp(Instant.ofEpochSecond(end()));
        entity.setDuration(Duration.ofSeconds(duration()));
        entity.setPeriod(Period.ofDays(Math.toIntExact(periodAsDays())));
        return entity;
    }

    public @NotNull Long periodAsDays()
    {
        long days = (period() / 60 / 60 / 24);
        if (days < 1)
        {
            throw new CreationException(HttpStatus.BAD_REQUEST);
        }
        return days;
    }
}
