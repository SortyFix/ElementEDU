package de.gaz.eedu.course.appointment.scheduled;

import de.gaz.eedu.course.CourseEntity;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service @RequiredArgsConstructor @Getter(AccessLevel.PROTECTED)
public class ScheduledAppointmentService extends EntityService<ScheduledAppointmentRepository, ScheduledAppointmentEntity, ScheduledAppointmentModel, ScheduledAppointmentCreateModel>
{
    private final ScheduledAppointmentRepository repository;
    private final CourseService courseService;

    @Transactional @Override public @NotNull List<ScheduledAppointmentEntity> createEntity(@NotNull Set<ScheduledAppointmentCreateModel> model) throws CreationException
    {
        Set<CourseEntity> editedCourses = new HashSet<>();
        List<ScheduledAppointmentEntity> scheduledAppointmentEntities = model.stream().map(current ->
        {
            ScheduledAppointmentEntity entity = current.toEntity(new ScheduledAppointmentEntity());
            CourseEntity courseEntity = getCourseService().loadEntityByIDSafe(current.course());
            editedCourses.add(courseEntity);
            courseEntity.scheduleRepeating(entity);
            return entity;
        }).toList();

        //save courses
        getCourseService().saveEntity(editedCourses);
        return scheduledAppointmentEntities;
    }

    @Override public @NotNull Optional<ScheduledAppointmentModel> loadById(long id)
    {
        return super.loadById(id);
    }
}
