package de.gaz.eedu.course.appointment.scheduled;

import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.CourseService;
import de.gaz.eedu.course.appointment.scheduled.model.InternalFrequentAppointmentCreateModel;
import de.gaz.eedu.course.appointment.scheduled.model.ScheduledAppointmentModel;
import de.gaz.eedu.course.room.RoomEntity;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service @RequiredArgsConstructor @Getter(AccessLevel.PROTECTED)
public class ScheduledAppointmentService extends EntityService<ScheduledAppointmentRepository, ScheduledAppointmentEntity, ScheduledAppointmentModel, InternalFrequentAppointmentCreateModel>
{
    private final ScheduledAppointmentRepository repository;
    private final CourseService courseService;

    @Transactional @Override
    public @NotNull List<ScheduledAppointmentEntity> createEntity(@NotNull Set<InternalFrequentAppointmentCreateModel> model) throws CreationException
    {
        return saveEntity(model.stream().map(current ->
        {
            CourseEntity courseEntity = getCourseService().loadEntityByIDSafe(current.courseId());
            return current.toEntity(new ScheduledAppointmentEntity(), (entity) ->
            {
                try {
                    RoomEntity room = getCourseService().getRoomService().loadEntityByIDSafe(current.data().room());
                    entity.setRoom(room);
                    entity.setCourse(courseEntity);
                    return entity;
                }
                catch (EntityUnknownException entityUnknownException)
                {
                    throw new RuntimeException(entityUnknownException);
                }
                // TODO give meaningful error codes
            });
        }).toList());
    }


    @Override public @NotNull Optional<ScheduledAppointmentModel> loadById(long id)
    {
        return super.loadById(id);
    }
}
