package de.gaz.eedu.course.appointment.model;

import de.gaz.eedu.course.appointment.CourseAppointmentEntity;
import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;

public record CourseAppointmentCreateModel(@NotNull Long start, @NotNull Long period, @NotNull Long course) implements CreationModel<CourseAppointmentEntity>
{

    @Override public @NotNull CourseAppointmentEntity toEntity(@NotNull CourseAppointmentEntity entity)
    {
        entity.setTimeStamp(start());
        entity.setPeriod(period());
        return entity;
    }
}
