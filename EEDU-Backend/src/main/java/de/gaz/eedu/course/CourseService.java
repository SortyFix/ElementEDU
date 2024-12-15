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

@RequiredArgsConstructor @Service @Getter(AccessLevel.PROTECTED)
public class CourseService extends EntityService<CourseRepository, CourseEntity, CourseModel, CourseCreateModel>
{
    private final CourseRepository repository;
    private final SubjectService subjectService;
    private final UserRepository userRepository;
    private final ClassRoomRepository classRoomRepository;
    private final AppointmentEntryRepository appointmentEntryRepository;
    private final FileService fileService;

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

    public @NotNull CourseModel[] getCourses(long user)
    {
        UserEntity userEntity = getUserRepository().findById(user).orElseThrow();
        return userEntity.getCourses().stream().map(CourseEntity::toModel).toArray(CourseModel[]::new);
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
     * @param createModel entry blueprint
     * @param course the course they are attached to.
     * @return the newly created entity.
     */
    private @NotNull AppointmentEntryEntity createAppointmentUnsafe(long id, @NotNull AppointmentEntryCreateModel createModel, @NotNull CourseEntity course)
    {
        AppointmentEntryRepository entryRepository = getAppointmentEntryRepository();

        AppointmentEntryEntity entity = new AppointmentEntryEntity(id, course);
        return entryRepository.save(createModel.toEntity(entity, entryEntity ->
        {
            if (Objects.isNull(createModel.duration()))
            {
                return attachScheduled(createModel, course, entryEntity);
            }
            entryEntity.setDuration(Duration.ofSeconds(createModel.duration()));
            return entryEntity;
        }));
    }

}
