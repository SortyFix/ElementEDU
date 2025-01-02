package de.gaz.eedu.course;

import de.gaz.eedu.course.appointment.entry.AppointmentEntryEntity;
import de.gaz.eedu.course.appointment.entry.AppointmentEntryRepository;
import de.gaz.eedu.course.appointment.entry.model.AppointmentEntryCreateModel;
import de.gaz.eedu.course.appointment.entry.model.AppointmentEntryModel;
import de.gaz.eedu.course.appointment.scheduled.ScheduledAppointmentEntity;
import de.gaz.eedu.course.classroom.ClassRoomRepository;
import de.gaz.eedu.course.model.CourseCreateModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.course.room.RoomEntity;
import de.gaz.eedu.course.room.RoomService;
import de.gaz.eedu.course.subject.SubjectService;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
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
import java.util.*;
import java.util.stream.Stream;

@RequiredArgsConstructor @Service @Getter(AccessLevel.PROTECTED)
public class CourseService extends EntityService<CourseRepository, CourseEntity, CourseModel, CourseCreateModel>
{
    private final CourseRepository repository;
    private final SubjectService subjectService;
    @Getter(AccessLevel.PUBLIC)
    private final RoomService roomService;
    private final UserRepository userRepository;
    private final ClassRoomRepository classRoomRepository;
    @Getter(AccessLevel.PUBLIC)
    private final AppointmentEntryRepository appointmentRepository;
    private final FileService fileService;

    @Contract(pure = true, value = "_,_,_ -> param3")
    private static @NotNull AppointmentEntryEntity attachScheduled(@NotNull AppointmentEntryCreateModel entryCreateModel, @NotNull CourseEntity course, @NotNull AppointmentEntryEntity entity) throws CreationException
    {
        Set<ScheduledAppointmentEntity> scheduledAppointments = course.getScheduledAppointments();
        Instant timeStamp = Instant.ofEpochSecond(entryCreateModel.start());
        return scheduledAppointments.stream().filter(event -> event.inFrequency(timeStamp)).map(event ->
        {
            entity.setDuration(event.getDuration());
            entity.setRoom(event.getRoom());
            entity.setScheduledAppointment(event);
            return entity;
        }).findFirst().orElseThrow(() -> new CreationException(HttpStatus.BAD_REQUEST));
    }

    public @NotNull CourseModel[] getCourses(long user)
    {
        return getRepository().findAllByUserId(user).stream().map(CourseEntity::toModel).toArray(CourseModel[]::new);
    }

    @Contract(pure = true, value = "_, _ -> _")
    private static long generateId(@NotNull Long timeStamp, @NotNull CourseEntity entity)
    {
        return Objects.hash(entity.getId(), timeStamp);
    }

    @Transactional @Override public @NotNull List<CourseEntity> createEntity(@NotNull Set<CourseCreateModel> model) throws CreationException
    {
        if(getRepository().existsByNameIn(model.stream().map(CourseCreateModel::name).toList()))
        {
            throw new OccupiedException();
        }

        Set<FileEntity> repositories = new HashSet<>(model.size());
        List<CourseEntity> courseEntities = model.stream().map(clazzModel ->
        {
            //TODO Yonas: please add a way of creating entities without instantly saving them
            FileCreateModel file = new FileCreateModel(1L, clazzModel.name(), new String[0], "", new String[0]);
            FileEntity fileEntity = file.toEntity(new FileEntity());
            repositories.add(fileEntity);

            return clazzModel.toEntity(new CourseEntity(fileEntity), (entity) ->
            {
                // attach users
                if (clazzModel.users().length > 0)
                {
                    List<UserEntity> userEntities = getUserRepository().findAllById(List.of(clazzModel.users()));
                    entity.attachUsers(userEntities.toArray(UserEntity[]::new));
                }

                // assign class
                if (clazzModel.classId() != null)
                {
                    getClassRoomRepository().findById(clazzModel.classId()).ifPresentOrElse(entity::linkClassRoom, () ->
                    {
                        throw new EntityUnknownException(clazzModel.classId());
                    });
                }

                // add to subject
                entity.setSubject(getSubjectService().loadEntityByIDSafe(clazzModel.subjectId()));
                return entity;
            });
        }).toList();

        // create repositories first
        getFileService().getRepository().saveAll(repositories);
        return saveEntity(courseEntities);
    }

    /**
     * Retrieves an {@link AppointmentEntryEntity} based on the given timestamp and course id.
     * <p>
     * This method attempts to find an appointment entry for the specified course and timestamp.
     * If no entry exists, it creates a new one using the provided timestamp and saves it.
     *
     * @param timeStamp the timestamp of the desired appointment.
     * @param courseId  the id of the course associated with the appointment.
     * @return a non-null {@link AppointmentEntryEntity}, either retrieved or newly created.
     */
    @Transactional public @NotNull AppointmentEntryEntity getAppointment(@NotNull Long timeStamp, @NotNull Long courseId)
    {
        CourseEntity course = loadEntityByIDSafe(courseId);
        return getInternalAppointment(course, timeStamp).orElseGet(() -> {
            AppointmentEntryCreateModel createModel = new AppointmentEntryCreateModel(timeStamp);
            return getAppointmentRepository().save(createAppointmentUnsafe(course, Set.of(createModel)).getFirst());
        });
    }

    /**
     * Creates and persists a list of {@link AppointmentEntryEntity} based on the provided course id and creation models.
     * <p>
     * This method retrieves the course entity using the specified course id, generates appointment entries from the provided
     * creation models, saves them in the repository, and converts the saved entities to their corresponding models.
     *
     * @param courseId         the id of the course for which the appointments are to be created.
     * @param entryCreateModel the set of models containing details for appointment creation.
     * @return a list of {@link AppointmentEntryModel} representing the created and saved appointments.
     */
    @Transactional public @NotNull List<AppointmentEntryModel> createAppointment(@NotNull Long courseId, @NotNull Set<AppointmentEntryCreateModel> entryCreateModel)
    {
        CourseEntity courseEntity = loadEntityByIDSafe(courseId);
        List<AppointmentEntryEntity> entities = createAppointmentUnsafe(courseEntity, entryCreateModel);
        return getAppointmentRepository().saveAll(entities).stream().map(AppointmentEntryEntity::toModel).toList();
    }

    /**
     * Creates a list of {@link AppointmentEntryEntity} based on the given course and creation models.
     * <p>
     * This method maps the provided creation models to {@link AppointmentEntryEntity} instances,
     * attaching them to the specified course. The entities are not saved in the repository.
     * <p>
     * Note! This method does not save the created objects.
     * {@link #getAppointmentRepository()} can be used to save the created instances.
     *
     * @param course     the {@link CourseEntity} to which the appointments are attached.
     * @param createModel the set of models containing details for appointment creation.
     * @return a list of {@link AppointmentEntryEntity} created from the models.
     */
    private @NotNull List<AppointmentEntryEntity> createAppointmentUnsafe(CourseEntity course, @NotNull Set<AppointmentEntryCreateModel> createModel)
    {
        return createModel.stream().map((currentModel) -> {
            long id = generateId(currentModel.start(), course);

            return currentModel.toEntity(new AppointmentEntryEntity(id), (entity) -> {
                if (Objects.isNull(currentModel.duration()))
                {
                    return attachScheduled(currentModel, course, entity);
                }
                entity.setDuration(Duration.ofMillis(currentModel.duration()));
                entity.setCourse(course);

                if(Objects.nonNull(currentModel.room()))
                {
                    RoomEntity room = getRoomService().loadEntityByIDSafe(currentModel.room());
                    entity.setRoom(room);
                }

                return entity;
            });
        }).toList();
    }

    /**
     * Retrieves an {@link Optional} of {@link AppointmentEntryEntity} for the given course and timestamp.
     * <p>
     * This method searches the entries of the specified course to find an appointment with the given timestamp.
     *
     * @param course    the {@link CourseEntity} to search for the appointment.
     * @param timeStamp the timestamp of the desired appointment.
     * @return an {@link Optional} containing the {@link AppointmentEntryEntity} if found or an empty {@link Optional}.
     */
    private @NotNull Optional<AppointmentEntryEntity> getInternalAppointment(@NotNull CourseEntity course, long timeStamp)
    {
        long id = generateId(timeStamp, course);
        Stream<AppointmentEntryEntity> entries = Arrays.stream(course.getEntries());
        return entries.filter(entry -> Objects.equals(entry.getId(), id)).findFirst();
    }
}
