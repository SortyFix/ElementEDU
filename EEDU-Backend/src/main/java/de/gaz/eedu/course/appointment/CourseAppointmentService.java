package de.gaz.eedu.course.appointment;

import de.gaz.eedu.course.CourseRepository;
import de.gaz.eedu.course.appointment.model.CourseAppointmentCreateModel;
import de.gaz.eedu.course.appointment.model.CourseAppointmentModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor @Getter(AccessLevel.PROTECTED)
public class CourseAppointmentService extends EntityService<CourseAppointmentRepository, CourseAppointmentEntity, CourseAppointmentModel, CourseAppointmentCreateModel>
{
    private final CourseAppointmentRepository repository;
    private final CourseRepository courseRepository;

    @Transactional @Override public @NotNull CourseAppointmentEntity createEntity(@NotNull CourseAppointmentCreateModel model) throws CreationException
    {
        CourseAppointmentEntity appointment = model.toEntity(new CourseAppointmentEntity());
        getCourseRepository().getReferenceById(model.course()).setAppointment(appointment);
        return saveEntity(appointment);
    }
}
