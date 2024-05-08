package de.gaz.eedu.course.appointment.model;

import de.gaz.eedu.course.appointment.CourseAppointmentEntity;
import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.exception.CreationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;

public record CourseAppointmentCreateModel(@NotNull Long start, @NotNull Long duration, @NotNull Long period,
                                           @NotNull Long course) implements CreationModel<CourseAppointmentEntity>
{

    @Override public @NotNull CourseAppointmentEntity toEntity(@NotNull CourseAppointmentEntity entity)
    {
        entity.setTimeStamp(Instant.ofEpochSecond(start()));
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
