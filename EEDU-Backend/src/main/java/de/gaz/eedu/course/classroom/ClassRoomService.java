package de.gaz.eedu.course.classroom;

import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.CourseService;
import de.gaz.eedu.course.classroom.model.ClassRoomCreateModel;
import de.gaz.eedu.course.classroom.model.ClassRoomModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.AccountType;
import de.gaz.eedu.user.UserEntity;
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
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Set;
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
 Directly implementing {@link de.gaz.eedu.user.UserService} is not possible due to a circular dependency:
 {@link de.gaz.eedu.user.UserService} depends on this service, which in turn provides functions required by {@link de.gaz.eedu.user.UserService}.
 *
 * @see ClassRoomRepository
 * @see ClassRoomEntity
 * @see ClassRoomModel
 * @see ClassRoomCreateModel
 * @see EntityService
 * @author Ivo Quiring
 */
@Slf4j
@RequiredArgsConstructor @Service @Getter
public class ClassRoomService extends EntityService<Long, ClassRoomRepository, ClassRoomEntity, ClassRoomModel, ClassRoomCreateModel>
{
    @Getter(AccessLevel.PROTECTED) private final ClassRoomRepository repository;
    private final CourseService courseService;
    private final UserRepository userRepository;

    @Transactional
    @Override public @NotNull List<ClassRoomEntity> createEntity(@NotNull Set<ClassRoomCreateModel> model) throws CreationException
    {
        if (getRepository().existsByNameIn(model.stream().map(ClassRoomCreateModel::name).toList()))
        {
            throw new OccupiedException();
        }

        List<ClassRoomEntity> entities = saveEntity(model.stream().map(classroomFactory()).toList());

        // safe external managed relations
        getUserRepository().saveAllEntities(entities.stream().flatMap(this::getUsers).toList());
        getCourseService().saveEntity(entities.stream().flatMap(clazz -> clazz.getCourses().stream()).toList());

        return entities;
    }

    @Override public void deleteRelations(@NotNull ClassRoomEntity entry)
    {
        // remove this classroom from all courses and users
        getCourseService().saveEntity(entry.getCourses().stream().peek(CourseEntity::unlinkClassRoom).toList());
        getUserRepository().saveAllEntities(getUsers(entry).peek(user -> user.setClassRoom(null)).toList());
    }

    private @NotNull Stream<UserEntity> getUsers(@NotNull ClassRoomEntity classRoom)
    {
        Stream<UserEntity> students = classRoom.getStudents().stream();
        return Stream.concat(students, Stream.of(classRoom.getTutor()));
    }

    @Contract(pure = true, value = "-> new")
    private @NotNull Function<ClassRoomCreateModel, ClassRoomEntity> classroomFactory()
    {
        return current ->
        {
            UserEntity tutor = fetchTutor(current.tutor());
            List<UserEntity> fetchedUsers = getUserRepository().findAllById(List.of(current.students()));
            Collection<UserEntity> users = Stream.concat(Stream.of(tutor), fetchedUsers.stream()).toList();
            Collection<CourseEntity> courses = getCourseService().loadEntityById(current.courses());

            ClassRoomEntity classRoomEntity = new ClassRoomEntity(courses, users);
            tutor.setClassRoom(classRoomEntity);
            fetchedUsers.forEach(userEntity -> userEntity.setClassRoom(classRoomEntity));
            courses.forEach(course -> course.linkClassRoom(classRoomEntity));
            return current.toEntity(classRoomEntity);
        };
    }

    public @NotNull @Unmodifiable Set<CourseModel> getCourses(long user, long classroomId)
    {
        if(hasRole("ADMINISTRATOR") || getRepository().existsUserInCourse(user, classroomId))
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
     * @throws ResponseStatusException
     *      <ul>
     *          <li>
     *              {@link EntityUnknownException} is thrown when the given tutorId
     *              does not represent any existing {@link UserEntity} in the {@link #getUserRepository()}
     *          </li>
     *          <li>
     *              {@link ResponseStatusException} is thrown when the fetches user's {@link AccountType} is not
     *              equal to {@link AccountType#TEACHER}.
     *          </li>
     *      </ul>
     * @see #getUserRepository()
     * @see UserEntity#getAccountType()
     */
    private @NotNull UserEntity fetchTutor(long tutorId) throws ResponseStatusException
    {
        UserEntity tutor = getUserRepository().findById(tutorId).orElseThrow(() -> new EntityUnknownException(tutorId));
        if(!tutor.getAccountType().equals(AccountType.TEACHER))
        {
            String error = "The given user's id %s does not represent a teachers account.";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(error, tutorId));
        }
        return tutor;
    }
}
