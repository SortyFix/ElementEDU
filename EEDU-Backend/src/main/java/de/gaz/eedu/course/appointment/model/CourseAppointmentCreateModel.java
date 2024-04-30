package de.gaz.eedu.course.appointment.model;

import de.gaz.eedu.course.appointment.CourseAppointmentEntity;
import de.gaz.eedu.course.appointment.WeekDay;
import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;

public record CourseAppointmentCreateModel(@NotNull WeekDay weekDay, @NotNull Long course) implements CreationModel<CourseAppointmentEntity>
{

    @Override public @NotNull CourseAppointmentEntity toEntity(@NotNull CourseAppointmentEntity entity)
    {
        entity.setWeekDay(weekDay());
        return entity;
    }
}
