package de.gaz.eedu.course.appointment.scheduled;

import de.gaz.eedu.course.CourseService;
import de.gaz.eedu.course.appointment.scheduled.model.ScheduledAppointmentCreateModel;
import de.gaz.eedu.course.appointment.scheduled.model.ScheduledAppointmentModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service @RequiredArgsConstructor @Getter(AccessLevel.PROTECTED)
public class ScheduledAppointmentService extends EntityService<ScheduledAppointmentRepository, ScheduledAppointmentEntity, ScheduledAppointmentModel, ScheduledAppointmentCreateModel>
{
    private final ScheduledAppointmentRepository repository;
    private final CourseService courseService;

    @Transactional @Override public @NotNull ScheduledAppointmentEntity[] createEntity(@NotNull ScheduledAppointmentCreateModel... model) throws CreationException
    {
        return Stream.of(model).map(current ->
        {
            ScheduledAppointmentEntity entity = current.toEntity(new ScheduledAppointmentEntity());
            getCourseService().loadEntityByIDSafe(current.course()).scheduleRepeating(entity);
            return entity;
        }).toList().toArray(new ScheduledAppointmentEntity[model.length]);
    }
}
