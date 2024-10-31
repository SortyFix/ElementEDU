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
import de.gaz.eedu.file.FileCreateModel;
import de.gaz.eedu.file.FileEntity;
import de.gaz.eedu.file.FileService;
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
    private final FileService fileService;

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
    private static long generateId(@NotNull Long timeStamp, @NotNull CourseEntity entity)
    {
        return (entity.getId() + "-" + timeStamp).hashCode();
    }

    @Transactional @Override public @NotNull CourseEntity createEntity(@NotNull CourseCreateModel model) throws CreationException
    {
        if (getRepository().existsByName(model.name()))
        {
            throw new NameOccupiedException(model.name());
        }

        // WHY DO I HAVE TO ADD AN AUTHOR AND A FILE NAME????????? YONAAAAAASSSSSSSSSSSS
        FileCreateModel file = new FileCreateModel(1L, model.name(), new String[0], "", new String[0]);
        FileEntity fileEntity = getFileService().createEntity(file);

        return saveEntity(model.toEntity(new CourseEntity(fileEntity), (entity) ->
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
                getClassRoomRepository().findById(model.classId()).ifPresentOrElse(entity::linkClassRoom, () ->
                {
                    throw new EntityUnknownException(model.classId());
                });
            }

            // add to subject
            entity.setSubject(getSubjectService().loadEntityByIDSafe(model.subjectId()));
            return entity;
        }));
    }

    @Transactional public @NotNull AppointmentEntryEntity getAppointment(@NotNull Long timeStamp, @NotNull Long courseId)
    {
        CourseEntity course = loadEntityByIDSafe(courseId);
        long id = generateId(courseId, course);

        return Arrays.stream(course.getEntries()).filter(entry -> Objects.equals(entry.getId(), id)).findFirst().orElseGet(() -> {

            AppointmentEntryCreateModel createModel = new AppointmentEntryCreateModel(timeStamp);
            return createAppointmentUnsafe(id, createModel, course);
        });
    }

    @Transactional public void createAppointment(@NotNull Long courseId, @NotNull AppointmentEntryCreateModel entryCreateModel)
    {
        CourseEntity courseEntity = loadEntityByIDSafe(courseId);

        long id = generateId(entryCreateModel.timeStamp(), courseEntity);
        if (Arrays.stream(courseEntity.getEntries()).anyMatch(entry -> Objects.equals(entry.getId(), id)))
        {
            throw new OccupiedException();
        }

        // intentionally ignore return value
        createAppointmentUnsafe(id, entryCreateModel, courseEntity);
    }

    /**
     * This method generates a new {@link AppointmentEntryEntity}.
     * <p>
     * Generates a new {@link AppointmentEntryEntity} from a {@link AppointmentEntryCreateModel}. Unlike {@link #createAppointment(Long, AppointmentEntryCreateModel)}
     * this method wont check whether it will override an exiting appointment entry.
     *  TODO I'll improve the doc once I get home
     *
     * @param id already computed before. Will be the id of the entity
     * @param entryCreateModel entry blueprint
     * @param course the course they are attached to.
     * @return the newly created entity.
     */
    private @NotNull AppointmentEntryEntity createAppointmentUnsafe(long id, @NotNull AppointmentEntryCreateModel entryCreateModel, @NotNull CourseEntity course)
    {
        CreationFactory<AppointmentEntryEntity> factory = createEntity(entryCreateModel, course);
        AppointmentEntryEntity entry = entryCreateModel.toEntity(new AppointmentEntryEntity(id), factory);
        getAppointmentEntryRepository().save(entry);

        course.setEntry(this, entry);
        return entry;
    }

}
