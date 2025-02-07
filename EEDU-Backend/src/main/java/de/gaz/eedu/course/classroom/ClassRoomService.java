package de.gaz.eedu.course.classroom;

import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.CourseService;
import de.gaz.eedu.course.classroom.model.ClassRoomCreateModel;
import de.gaz.eedu.course.classroom.model.ClassRoomModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.entity.model.CreationFactory;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.AccountType;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Set;

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
@RequiredArgsConstructor @Service @Getter
public class ClassRoomService extends EntityService<ClassRoomRepository, ClassRoomEntity, ClassRoomModel, ClassRoomCreateModel>
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

        return saveEntity(model.stream().map(current ->
        {
            ClassRoomEntity classRoomEntity = new ClassRoomEntity(getCourseService().loadEntityById(current.courses()));
            return current.toEntity(classRoomEntity, this.classroomFactory(current));
        }).toList());
    }

    @Override public void deleteRelations(@NotNull ClassRoomEntity entry)
    {
        // remove this classroom from all courses
        Set<CourseEntity> courses = entry.getCourses();
        courses.forEach(CourseEntity::unlinkClassRoom);
        getCourseService().saveEntity(courses);
    }

    /**
     * Provides a stateless {@link CreationFactory}.
     * <p>
     * This method provides a {@link CreationFactory} that is used to apply changes to the entity after it has been
     * created. This is required for operations that require access to other repositories or services. In this specific
     * case it handles two major components which includes the following:
     * <ul>
     *     <li>Setting and fetching the tutor {@link UserEntity}, with {@link #fetchTutor(long)}, if {@link ClassRoomCreateModel#tutor()} is not null.</li>
     *     <li>Fetching all {@link UserEntity}s inside of the createModel and attaching them to the newly created class</li>
     * </ul>
     * <p>
     * Note! This returns a function and therefore is stateless.
     *
     * @param createModel current model which provides details of the newly created entity
     * @return a stateless function which sets the required attributes of the passed create model.
     */
    @Contract(pure = true, value = "_ -> new")
    private @NotNull CreationFactory<ClassRoomEntity> classroomFactory(@NotNull ClassRoomCreateModel createModel)
    {
        return (entity ->
        {
            // attach users and tutor
            Long tutorId = createModel.tutor();
            if(Objects.nonNull(tutorId))
            {
                entity.setTutor(fetchTutor(tutorId));
            }

            if (createModel.students().length > 0)
            {
                List<UserEntity> userEntities = getUserRepository().findAllById(List.of(createModel.students()));
                entity.attachStudents(userEntities.toArray(UserEntity[]::new));
            }

            return entity;
        });
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The given user's account type is not equal to teacher.");
        }
        return tutor;
    }
}
