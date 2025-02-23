package de.gaz.eedu.course.appointment;

import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.CourseRepository;
import de.gaz.eedu.course.appointment.entry.AppointmentEntryEntity;
import de.gaz.eedu.course.appointment.entry.AppointmentEntryRepository;
import de.gaz.eedu.course.appointment.entry.model.*;
import de.gaz.eedu.course.appointment.frequent.FrequentAppointmentEntity;
import de.gaz.eedu.course.appointment.frequent.FrequentAppointmentRepository;
import de.gaz.eedu.course.appointment.frequent.model.FrequentAppointmentModel;
import de.gaz.eedu.course.appointment.frequent.model.InternalFrequentAppointmentCreateModel;
import de.gaz.eedu.course.room.RoomEntity;
import de.gaz.eedu.course.room.RoomRepository;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class AppointmentService extends EntityService<Long, FrequentAppointmentRepository, FrequentAppointmentEntity, FrequentAppointmentModel, InternalFrequentAppointmentCreateModel>
{
    private final FrequentAppointmentRepository repository;
    @Getter(AccessLevel.PUBLIC)
    private final AppointmentEntryRepository entryRepository;
    private final CourseRepository courseRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Contract(pure = true, value = "_, _ -> _")
    private static long generateId(@NotNull CourseEntity entity, @NotNull Long timeStamp)
    {
        // okay so, this is a bit complex, so I'll break it down
        // Firstly, I bitshift the courseId into the lower 16 bits, because a long is 64 bits in total and
        // the id is never as high as the timestamp.
        // That means 65535 courses can be created, before the system breaks apart.

        // Secondly, I shift the timestamp into the 48 remaining bits as it is also a long value
        // The value 0xFFFFFFFFFFFFL is a 48-bit mask in hexadecimal format

        // The 0x indicates it's a hexadecimal number, where each F are 4 bits (there are 12),
        // making up a total of 48 bits. The L suffix defines that the value is of type Long

        // This allows me to encode both the course id and the timestamp into a single id
        return (entity.getId() << 48) | ((timeStamp / 100) & 0xFFFFFFFFFFFFL);
    }

    @Contract(pure = true)
    private static boolean setAssignment(@NotNull AppointmentEntryEntity entity, @Nullable AssignmentCreateModel assignment)
    {
        if (Objects.isNull(assignment))
        {
            return true;
        }

        if (Objects.isNull(assignment.description()))
        {
            return entity.unsetAssignment();
        }

        return entity.setAssignment(assignment);
    }

    public @NotNull List<AssignmentInsightModel> getInsight(long appointment)
    {
        return getEntryRepository().findById(appointment).map(entry ->
        {
            Set<UserEntity> userEntities = entry.getCourse().getStudents();
            return userEntities.stream().map(entry::getInsight).toList();
        }).orElse(Collections.emptyList());
    }

    public @NotNull Optional<AssignmentInsightModel> getInsight(long appointment, long user)
    {
        return getEntryRepository().findById(appointment).map(entry ->
        {
            UserEntity userEntity = getUserRepository().findEntity(user).orElseThrow(entityUnknown(user));
            return entry.getInsight(userEntity);
        });
    }

    public @NotNull AppointmentEntryModel update(long appointmentId, @NotNull AppointmentUpdateModel updateModel)
    {
        AppointmentEntryEntity entity = getEntryRepository().findById(appointmentId).orElseThrow(entityUnknown(appointmentId));

        int hash = entity.hashCode();
        entity.setDescription(updateModel.description());

        // doesn't the id match the room id, or is the room id not null
        Function<RoomEntity, Boolean> equals = (room -> !Objects.equals(updateModel.room(), room.getId()));
        if (entity.getRoom().map(equals).orElseGet(() -> Objects.nonNull(updateModel.room())))
        {
            entity.setRoom(
                    Objects.isNull(updateModel.room()) ? null :
                            roomRepository.findById(updateModel.room()).orElseThrow(entityUnknown(updateModel.room()))
            );
        }

        if (!setAssignment(entity, updateModel.assignment()))
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Assignment could not be updated");
        }

        if (Objects.equals(hash, entity.hashCode()))
        {
            getEntryRepository().save(entity);
        }

        return entity.toModel();
    }

    public boolean unscheduleFrequent(long courseId, @NotNull Long... entities)
    {
        CourseEntity course = getCourse(courseId);
        boolean response = course.unscheduleFrequent(entities);
        getCourseRepository().save(course);
        return response;
    }

    @Transactional @Override
    public @NotNull List<FrequentAppointmentEntity> createEntity(@NotNull Set<InternalFrequentAppointmentCreateModel> model) throws CreationException
    {
        return saveEntity(model.stream().map(current ->
        {
            CourseEntity courseEntity = getCourse(current.courseId());
            return current.toEntity(
                    new FrequentAppointmentEntity(), (entity) ->
                    {
                        RoomEntity room = getRoom(current.data().room());
                        entity.setRoom(room);
                        entity.setCourse(courseEntity);
                        return entity;
                    });
        }).toList());
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
    @Transactional
    public @NotNull AppointmentEntryEntity getAppointment(@NotNull Long timeStamp, @NotNull Long courseId)
    {
        CourseEntity course = getCourse(courseId);
        return getInternalAppointment(course, timeStamp).orElseGet(() ->
        {
            AppointmentEntryCreateModel createModel = new AppointmentEntryCreateModel(timeStamp);
            return getEntryRepository().save(createAppointmentUnsafe(course, Set.of(createModel)).getFirst());
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
    @Transactional
    public @NotNull List<AppointmentEntryModel> createAppointment(@NotNull Long courseId, @NotNull Set<AppointmentEntryCreateModel> entryCreateModel)
    {
        CourseEntity courseEntity = getCourse(courseId);
        List<AppointmentEntryEntity> entities = createAppointmentUnsafe(courseEntity, entryCreateModel);
        return getEntryRepository().saveAll(entities).stream().map(AppointmentEntryEntity::toModel).toList();
    }

    public void submitAssignment(long user, long assignment, @NotNull MultipartFile[] files)
    {
        Optional<AppointmentEntryEntity> entryReference = getEntryRepository().findById(assignment);
        AppointmentEntryEntity entry = entryReference.orElseThrow(entityUnknown(assignment));
        entry.submitAssignment(user, files);
    }

    /**
     * Creates a list of {@link AppointmentEntryEntity} based on the given course and creation models.
     * <p>
     * This method maps the provided creation models to {@link AppointmentEntryEntity} instances,
     * attaching them to the specified course. The entities are not saved in the repository.
     * <p>
     * Note! This method does not save the created objects.
     * {@link #getEntryRepository()} can be used to save the created instances.
     *
     * @param course      the {@link CourseEntity} to which the appointments are attached.
     * @param createModel the set of models containing details for appointment creation.
     * @return a list of {@link AppointmentEntryEntity} created from the models.
     */
    private @NotNull List<AppointmentEntryEntity> createAppointmentUnsafe(@NotNull CourseEntity course, @NotNull Set<AppointmentEntryCreateModel> createModel)
    {
        return createModel.stream().map((currentModel) ->
        {
            long id = generateId(course, currentModel.start());

            return currentModel.toEntity(
                    new AppointmentEntryEntity(id), (entity) ->
                    {
                        Instant time = Instant.ofEpochMilli(currentModel.start());
                        for (FrequentAppointmentEntity frequentAppointment : course.getFrequentAppointments())
                        {
                            if (frequentAppointment.inFrequency(time))
                            {
                                entity.setFrequentAppointment(frequentAppointment);

                                // these two below might get overridden by custom values
                                entity.setDuration(frequentAppointment.getDuration());
                                entity.setRoom(frequentAppointment.getRoom());

                                break;
                            }
                        }

                        if (Objects.nonNull(currentModel.duration()))
                        {
                            entity.setDuration(Duration.ofMillis(currentModel.duration()));
                        }

                        if (Objects.isNull(entity.getDuration()))
                        {
                            // duration MUST be set here already
                            throw new CreationException(HttpStatus.BAD_REQUEST);
                        }

                        if (Objects.nonNull(currentModel.room()))
                        {
                            RoomEntity room = getRoom(currentModel.room());
                            entity.setRoom(room);
                        }

                        entity.setCourse(course);
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
        long id = generateId(course, timeStamp);
        Stream<AppointmentEntryEntity> entries = Arrays.stream(course.getEntries());
        return entries.filter(entry -> Objects.equals(entry.getId(), id)).findFirst();
    }

    private @NotNull CourseEntity getCourse(@NotNull Long courseId) throws EntityUnknownException
    {
        return getCourseRepository().findById(courseId).orElseThrow(entityUnknown(courseId));
    }

    private @NotNull RoomEntity getRoom(@NotNull String roomId) throws EntityUnknownException
    {
        return getRoomRepository().findById(roomId).orElseThrow(entityUnknown(roomId));
    }
}
