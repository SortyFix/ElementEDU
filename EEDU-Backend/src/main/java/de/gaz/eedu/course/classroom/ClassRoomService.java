package de.gaz.eedu.course.classroom;

import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.CourseService;
import de.gaz.eedu.course.classroom.model.ClassRoomCreateModel;
import de.gaz.eedu.course.classroom.model.ClassRoomModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.AccountTypeMismatch;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.AccountType;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.model.ReducedUserModel;
import de.gaz.eedu.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This service handles the lifecycle management of {@link ClassRoomEntity}, including creation, deletion, and retrieval.
 * <p>
 * It extends the generic {@link EntityService} providing all common required functions such as
 * creating, deleting or retrieving {@link ClassRoomEntity} from the specified {@link ClassRoomRepository}.
 * <p>
 * Additionally it's important to note that this service uses some foreign services to achieve integrity to
 * maintain system integrity, such as the {@link CourseEntity} for linking {@link ClassRoomEntity}s to {@link CourseEntity}s and
 * the {@link UserRepository} for attaching {@link UserEntity} to {@link ClassRoomEntity}.
 * <p>
 * Directly implementing {@link de.gaz.eedu.user.UserService} is not possible due to a circular dependency:
 * {@link de.gaz.eedu.user.UserService} depends on this service, which in turn provides functions required by {@link de.gaz.eedu.user.UserService}.
 *
 * @author Ivo Quiring
 * @see ClassRoomRepository
 * @see ClassRoomEntity
 * @see ClassRoomModel
 * @see ClassRoomCreateModel
 * @see EntityService
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Getter
public class ClassRoomService extends EntityService<String, ClassRoomRepository, ClassRoomEntity, ClassRoomModel, ClassRoomCreateModel>
{
    @Getter(AccessLevel.PROTECTED) private final ClassRoomRepository repository;
    private final CourseService courseService;
    private final UserRepository userRepository;

    public @NotNull @Unmodifiable Set<ReducedUserModel> loadReducedModelsByClass(@NotNull String classroom)
    {
        return getRepository().findAllUsersByClass(classroom);
    }

    @Transactional public boolean linkClass(@PathVariable long course, @PathVariable String classroom)
    {
        ClassRoomEntity classRoom = loadEntityByIDSafe(classroom);
        return getCourseService().loadEntityByIDSafe(course).linkClassRoom(getCourseService(), classRoom);
    }

    @Transactional public boolean unlinkClass(@PathVariable long course)
    {
        return getCourseService().loadEntityByIDSafe(course).unlinkClassRoom(getCourseService());
    }

    @Transactional @Override public @NotNull List<ClassRoomEntity> createEntity(@NotNull Set<ClassRoomCreateModel> model) throws CreationException
    {
        if (getRepository().existsByIdIn(model.stream().map(ClassRoomCreateModel::id).toList()))
        {
            throw new OccupiedException();
        }

        return injectAndSaveRelations(saveEntity(model.stream().map(classroomFactory()).toList()));
    }

    @Override public void deleteRelations(@NotNull ClassRoomEntity entry)
    {
        // remove this classroom from all courses and users
        getCourseService().saveEntity(entry.getCourses().stream().peek(CourseEntity::unlinkClassRoom).toList());
        getUserRepository().saveAllEntities(getUsers(entry).peek(user -> user.setClassRoom(null)).toList());
    }

    private @NotNull @Unmodifiable List<ClassRoomEntity> injectAndSaveRelations(@NotNull List<ClassRoomEntity> entries)
    {
        // ensure the repository is flushed
        getRepository().flush();

        for (ClassRoomEntity classRoomEntity : entries)
        {
            classRoomEntity.getTutor().ifPresent(teacher -> teacher.setClassRoom(classRoomEntity));
            classRoomEntity.getStudents().forEach(student -> student.setClassRoom(classRoomEntity));
            classRoomEntity.getCourses().forEach(course -> course.linkClassRoom(classRoomEntity));
        }

        getUserRepository().saveAllEntities(entries.stream().flatMap(this::getUsers).toList());
        getCourseService().saveEntity(entries.stream().flatMap(clazz -> clazz.getCourses().stream()).toList());
        return entries;
    }

    private @NotNull Stream<UserEntity> getUsers(@NotNull ClassRoomEntity classRoom)
    {
        return Stream.concat(classRoom.getStudents().stream(), classRoom.getTutor().stream());
    }

    @Contract(pure = true, value = "-> new")
    private @NotNull Function<ClassRoomCreateModel, ClassRoomEntity> classroomFactory()
    {
        return current ->
        {
            UserEntity tutor = fetchTutor(current.tutor());
            List<UserEntity> fetchedUsers = getUserRepository().findAllById(List.of(current.students()));

            Collection<UserEntity> users = Stream.concat(Stream.of(tutor), fetchedUsers.stream()).toList();
            Collection<CourseEntity> courses = getCourseService().loadEntityById(Arrays.asList(current.courses()));

            return current.toEntity(new ClassRoomEntity(current.id(), courses, users));
        };
    }

    public @NotNull @Unmodifiable Set<CourseModel> getCourses(long user, long classroomId)
    {
        if (hasRole("ADMINISTRATOR") || getRepository().existsUserInCourse(user, classroomId))
        {
            return getRepository().findAllCoursesById(classroomId);
        }

        throw unauthorizedThrowable();
    }

    /**
     * Fetches a {@link UserEntity} and validates that its account type is {@link AccountType#TEACHER}.
     * <p>
     * This method fetches a {@link UserEntity} from the {@link #getUserRepository()} and then validates that the
     * user's account type is set to {@link AccountType#TEACHER}.
     *
     * @param tutorId id of the {@link UserEntity} to load.
     * @return the fetched and validated {@link UserEntity}
     * @throws ResponseStatusException <ul>
     *                                          <li>
     *                                              {@link EntityUnknownException} is thrown when the given tutorId
     *                                              does not represent any existing {@link UserEntity} in the {@link #getUserRepository()}
     *                                          </li>
     *                                          <li>
     *                                              {@link ResponseStatusException} is thrown when the fetches user's {@link AccountType} is not
     *                                              equal to {@link AccountType#TEACHER}.
     *                                          </li>
     *                                      </ul>
     * @see #getUserRepository()
     * @see UserEntity#getAccountType()
     */
    private @NotNull UserEntity fetchTutor(long tutorId) throws ResponseStatusException
    {
        UserEntity tutor = getUserRepository().findById(tutorId).orElseThrow(entityUnknown(tutorId));
        if (!Objects.equals(tutor.getAccountType(), AccountType.TEACHER))
        {
            throw new AccountTypeMismatch(AccountType.TEACHER, tutor.getAccountType());
        }
        return tutor;
    }
}
