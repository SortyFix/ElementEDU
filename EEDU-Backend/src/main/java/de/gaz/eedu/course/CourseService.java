package de.gaz.eedu.course;

import de.gaz.eedu.course.appointment.entry.AppointmentEntryEntity;
import de.gaz.eedu.course.appointment.entry.AppointmentEntryRepository;
import de.gaz.eedu.course.appointment.entry.model.AppointmentEntryCreateModel;
import de.gaz.eedu.course.appointment.scheduled.ScheduledAppointmentEntity;
import de.gaz.eedu.course.classroom.ClassRoomRepository;
import de.gaz.eedu.course.model.CourseCreateModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.course.subjects.SubjectService;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.entity.model.CreationFactory;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor @Service @Getter(AccessLevel.PROTECTED)
public class CourseService extends EntityService<CourseRepository, CourseEntity, CourseModel, CourseCreateModel>
{
    private final CourseRepository repository;
    private final SubjectService subjectService;
    private final UserRepository userRepository;
    private final ClassRoomRepository classRoomRepository;
    private final AppointmentEntryRepository appointmentEntryRepository;

    @Contract(pure = true, value = "_, _ -> new")
    private static @NotNull CreationFactory<AppointmentEntryEntity> createEntity(@NotNull AppointmentEntryCreateModel entryCreateModel, @NotNull CourseEntity course)
    {
        return entity ->
        {
            if (Objects.isNull(entryCreateModel.duration()))
            {
                return attachScheduled(entryCreateModel, course, entity);
            }
            entity.setDuration(Duration.ofSeconds(entryCreateModel.duration()));
            return entity;
        };
    }

    @Contract(pure = true, value = "_,_,_ -> param3")
    private static @NotNull AppointmentEntryEntity attachScheduled(@NotNull AppointmentEntryCreateModel entryCreateModel, @NotNull CourseEntity course, @NotNull AppointmentEntryEntity entity) throws CreationException
    {
        Set<ScheduledAppointmentEntity> scheduledAppointments = course.getScheduledAppointments();
        Instant timeStamp = Instant.ofEpochSecond(entryCreateModel.timeStamp());
        return scheduledAppointments.stream().filter(event -> event.inPeriod(timeStamp)).map(event ->
        {
            entity.setDuration(event.getDuration());
            entity.setScheduledAppointment(event);
            return entity;
        }).findFirst().orElseThrow(() -> new CreationException(HttpStatus.BAD_REQUEST));
    }

    @Contract(pure = true, value = "_, _ -> _")
    private static long generateId(@NotNull AppointmentEntryCreateModel entryCreateModel, @NotNull CourseEntity entity)
    {
        return (entity.getId() + "-" + entryCreateModel.timeStamp()).hashCode();
    }

    @Transactional @Override public @NotNull CourseEntity createEntity(@NotNull CourseCreateModel model) throws CreationException
    {
        if (getRepository().existsByName(model.name()))
        {
            throw new NameOccupiedException(model.name());
        }

        return saveEntity(model.toEntity(new CourseEntity(), (entity) ->
        {
            // attach users
            if(model.users().length > 0)
            {
                List<UserEntity> userEntities = getUserRepository().findAllById(List.of(model.users()));
                entity.attachUsers(userEntities.toArray(UserEntity[]::new));
            }

            // assign class
            if (model.classId() != null)
            {
                getClassRoomRepository().findById(model.classId()).ifPresentOrElse(entity::assignClassRoom, () ->
                {
                    throw new EntityUnknownException(model.classId());
                });
            }

            // add to subject
            entity.setSubject(getSubjectService().loadEntityByIDSafe(model.subjectId()));
            return entity;
        }));
    }

    @Transactional public void createAppointmentEntry(@NotNull Long courseId, @NotNull AppointmentEntryCreateModel entryCreateModel)
    {
        CourseEntity course = loadEntityByIDSafe(courseId);

        long id = generateId(entryCreateModel, course);
        if (Arrays.stream(course.getEntries()).anyMatch(entry -> Objects.equals(entry.getId(), id)))
        {
            throw new OccupiedException();
        }

        CreationFactory<AppointmentEntryEntity> factory = createEntity(entryCreateModel, course);
        AppointmentEntryEntity entry = entryCreateModel.toEntity(new AppointmentEntryEntity(id), factory);
        getAppointmentEntryRepository().save(entry);

        course.setEntry(this, entry);
    }
}
