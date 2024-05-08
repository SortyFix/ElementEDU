package de.gaz.eedu.course.appointment;

import de.gaz.eedu.course.CourseRepository;
import de.gaz.eedu.course.appointment.entry.AppointmentEntryEntity;
import de.gaz.eedu.course.appointment.entry.AppointmentEntryRepository;
import de.gaz.eedu.course.appointment.entry.model.AppointmentEntryCreateModel;
import de.gaz.eedu.course.appointment.model.CourseAppointmentCreateModel;
import de.gaz.eedu.course.appointment.model.CourseAppointmentModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.entity.model.CreationFactory;
import de.gaz.eedu.exception.CreationException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

@Service @RequiredArgsConstructor @Getter(AccessLevel.PROTECTED)
public class CourseAppointmentService extends EntityService<CourseAppointmentRepository, CourseAppointmentEntity, CourseAppointmentModel, CourseAppointmentCreateModel>
{
    private final CourseAppointmentRepository repository;
    private final CourseRepository courseRepository;
    private final AppointmentEntryRepository appointmentEntryRepository;

    private static @NotNull CreationFactory<AppointmentEntryEntity> entityFactory(@NotNull AppointmentEntryCreateModel entryCreateModel, CourseAppointmentEntity appointmentEntity)
    {
        return entity ->
        {
            if (Objects.isNull(entryCreateModel.duration()))
            {
                if (appointmentEntity.inPeriod(Instant.ofEpochSecond(entryCreateModel.timeStamp())))
                {
                    entity.setDuration(appointmentEntity.getDuration());
                    return entity;
                }
                throw new CreationException(HttpStatus.BAD_REQUEST);
            }
            entity.setDuration(Duration.ofSeconds(entryCreateModel.duration()));
            return entity;
        };
    }

    private static long generateId(@NotNull AppointmentEntryCreateModel entryCreateModel, @NotNull CourseAppointmentEntity appointment)
    {
        return (appointment.getId() + "-" + entryCreateModel.timeStamp()).hashCode();
    }

    @Transactional @Override public @NotNull CourseAppointmentEntity createEntity(@NotNull CourseAppointmentCreateModel model) throws CreationException
    {
        CourseAppointmentEntity appointment = model.toEntity(new CourseAppointmentEntity());
        getCourseRepository().getReferenceById(model.course()).addAppointment(appointment);
        return saveEntity(appointment);
    }

    @Transactional public void createAppointmentEntry(@NotNull Long appointmentId, @NotNull AppointmentEntryCreateModel entryCreateModel)
    {
        CourseAppointmentEntity appointment = loadEntityByIDSafe(appointmentId);

        long id = generateId(entryCreateModel, appointment);
        if (Arrays.stream(appointment.getEntries()).anyMatch(entry -> Objects.equals(entry.getId(), id)))
        {
            // TODO already existing
            return;
        }

        CreationFactory<AppointmentEntryEntity> factory = entityFactory(entryCreateModel, appointment);
        AppointmentEntryEntity entry = entryCreateModel.toEntity(new AppointmentEntryEntity(id), factory);
        getAppointmentEntryRepository().save(entry);

        appointment.setAppointmentEntry(this, entry);
    }
}
