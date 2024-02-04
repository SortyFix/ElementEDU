package de.gaz.eedu.course;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.gaz.eedu.course.classroom.ClassRoomEntity;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.course.subjects.SubjectEntity;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.model.UserModel;
import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a course entity in the system.
 * <p>
 * This class defines the structure of a course within the application, encapsulating information
 * such as the course name, associated users, and the subject to which the course is related.
 *
 * @author ivo
 */
@Entity @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Table(name = "course_entity")
public class CourseEntity implements EntityModelRelation<CourseModel>
{
    @ManyToMany @JsonManagedReference
    @JoinTable(name = "course_users", joinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private final Set<UserEntity> users = new HashSet<>();
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(AccessLevel.NONE) private Long id; // ID is final
    private String name;
    @ManyToOne @JsonManagedReference @JoinColumn(name = "class_room_id", referencedColumnName = "id")
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE) private ClassRoomEntity classRoom;

    @ManyToOne @JsonManagedReference @JoinColumn(name = "subject_id", referencedColumnName = "id")
    private SubjectEntity subject;

    @Override public CourseModel toModel()
    {
        return new CourseModel(getId(),
                getName(),
                getSubject().toModel(),
                getUsers().stream().map(UserEntity::toModel).toArray(UserModel[]::new));
    }

    /**
     * Attaches the specified user entities to the current group, modifying the provided {@code users} list.
     * <p>
     * This method attaches users to the current course group. It includes a filtering mechanism
     * to exclude users who are already attached to the group, ensuring that only unique and non-duplicate users are added.
     * The attachment process is facilitated through the provided {@link CourseService} instance, allowing for the saving of changes if necessary.
     * The {@code users} list is modified during the attachment process, representing the updated course membership.
     *
     * @param courseService the {@link CourseService} instance to be used for persisting changes if needed.
     * @param user          the array of {@link UserEntity} instances to be attached to the group.
     * @return true if the users were successfully attached, false otherwise.
     * @see #attachUser(UserEntity...)
     */
    public boolean attachUser(@NotNull CourseService courseService, @NonNull UserEntity... user)
    {
        return saveEntityIfPredicateTrue(courseService, user, this::attachUser);
    }

    /**
     * Attaches the specified user entities to the current group, ensuring uniqueness.
     * <p>
     * This method attaches users to the course group, filtering out those already attached based on their equality.
     * It ensures that only unique, non-duplicate users are added.
     * <p>
     * Note that this method does not persist the changes.
     * In order for the changes to be permanent this object needs be saved which can be archived by {@link #attachUser(CourseService, UserEntity...)},
     * or {@link CourseService#saveEntity(EntityModelRelation)}
     *
     * @param user The array of UserEntity instances to be attached to the group.
     * @return true if the users were successfully attached, false otherwise.
     * @see #attachUser(CourseService, UserEntity...)
     * @see CourseService#saveEntity(EntityModelRelation)
     */
    public boolean attachUser(@NonNull UserEntity... user)
    {
        // Filter already attached users out
        Predicate<UserEntity> predicate = present -> getUsers().stream().noneMatch(presentUser -> Objects.equals(
                presentUser,
                present));
        return this.users.addAll(Arrays.stream(user).filter(predicate).collect(Collectors.toSet()));
    }

    /**
     * Detaches the user entities with the specified IDs from the current group.
     * <p>
     * This method detaches users from this course based on their IDs. It uses the provided {@link CourseService}
     * instance to save the changes if necessary.
     * Note that only users which are part of this course are removed.
     *
     * @param courseService The {@link CourseService} instance to be used for saving the changes if necessary.
     * @param ids           The array of IDs corresponding to {@link UserEntity} instances to be detached from the group.
     * @return true if the users were successfully detached, false otherwise.
     * @see #detachUser(Long...)
     */
    public boolean detachUser(@NotNull CourseService courseService, @NonNull Long... ids)
    {
        return saveEntityIfPredicateTrue(courseService, ids, this::detachUser);
    }

    /**
     * Detaches the user entities with the specified IDs from the current group.
     * <p>
     * This method detaches users from this course based on their IDs.
     * Note that only users which are part of this course are removed.
     * <p>
     * To make the changes permanent, the object needs to be saved. This can be achieved by calling {@link #detachUser(CourseService, Long...)}
     * or {@link CourseService#saveEntity(EntityModelRelation)} after detaching the users.
     *
     * @param ids The array of IDs corresponding to {@link UserEntity} instances to be detached from the group.
     * @return true if the users were successfully detached, false otherwise.
     * @see #detachUser(Long...)
     * @see CourseService#saveEntity(EntityModelRelation)
     */
    public boolean detachUser(@NonNull Long... ids)
    {
        List<Long> detachGroupIds = Arrays.asList(ids);
        return this.users.removeIf(groupEntity -> detachGroupIds.contains(groupEntity.getId()));
    }

    /**
     * This method checks if a specific {@link UserEntity} is part of this course.
     * <p>
     * This method checks whether a {@link UserEntity} is part of this course or not.
     * It does this by checking the ids of each user from {@code getUsers()}.
     * <p>
     * Note that {@code getUsers()} also includes the {@link UserEntity} from the {@link ClassRoomEntity} if any
     * classroom has been set.
     *
     * @param id the id of the user which should be checked
     * @return whether this user is in the course or not.
     */
    public boolean inCourse(long id)
    {
        return getUsers().stream().anyMatch(user -> Objects.equals(user.getId(), id));
    }

    /**
     * Assigns a {@link ClassRoomEntity} to this course and saves the changes using the provided {@link CourseService}..
     * <p>
     * This method adds a {@link ClassRoomEntity} to the current course. It combines the users from the
     * {@link ClassRoomEntity#getUsers()} with the local {@code users}, accessible through {@link #getUsers()}.
     * <p>
     * It's important to note that this method uses the {@link CourseService} to persist changes.
     *
     * @param courseService The {@link CourseService} used to save the changes.
     * @param classRoom     The {@link ClassRoomEntity} to be associated with this course.
     * @return {@code true} if the association was successful, and changes were saved; false otherwise.
     */
    public boolean assignClassRoom(@NotNull CourseService courseService, @NotNull ClassRoomEntity classRoom)
    {
        return saveEntityIfPredicateTrue(courseService, classRoom, this::assignClassRoom);
    }

    /**
     * Assigns a {@link ClassRoomEntity} to this course.
     * <p>
     * This method adds a {@link ClassRoomEntity} to the current course. It combines the users from the
     * {@link ClassRoomEntity#getUsers()} with the local {@code users}, accessible through {@link #getUsers()}.
     *
     * @param classRoom The {@link ClassRoomEntity} to be associated with this course.
     * @return {@code true} if the association was successful, and changes were saved; false otherwise.
     */
    public boolean assignClassRoom(@NotNull ClassRoomEntity classRoom)
    {
        if (!Objects.equals(this.classRoom, classRoom))
        {
            this.classRoom = classRoom;
            return true;
        }
        return false;
    }

    /**
     * Disassociates the currently assigned {@link ClassRoomEntity} from this course and saves the changes using the provided {@link CourseService}.
     * <p>
     * This method calls {@link #disassociateClassroom()} to remove the association between the course and its assigned classroom.
     * The disassociation is persisted using the provided {@link CourseService}.
     *
     * @param courseService The {@link CourseService} used to save the changes.
     * @return {@code true} if the disassociation was successful, and changes were saved; false otherwise.
     */
    public boolean disassociateClassroom(@NotNull CourseService courseService)
    {
        // That's what I call an API stretch
        return saveEntityIfPredicateTrue(courseService, disassociateClassroom(), (value) -> value);
    }

    /**
     * Disassociates the currently assigned {@link ClassRoomEntity} from this course.
     * <p>
     * This method removes the association between the course and its assigned classroom by setting the
     * {@code classRoom} property to {@code null}.
     *
     * @return {@code true} if the disassociation was successful; false otherwise.
     */
    public boolean disassociateClassroom()
    {
        if (!hasClassRoomAssigned())
        {
            return false;
        }

        classRoom = null;
        return true;
    }

    /**
     * Retrieves the optional {@link ClassRoomEntity} assigned to this course, if any.
     * <p>
     * This method returns an {@link Optional} containing the assigned {@link ClassRoomEntity} if present,
     * or an empty {@link Optional} if no class is currently assigned to this course.
     *
     * @return An {@link Optional} containing the assigned {@link ClassRoomEntity} if present, otherwise an empty {@link Optional}.
     */
    public @NotNull Optional<ClassRoomEntity> getClassRoom()
    {
        return Optional.ofNullable(classRoom);
    }

    /**
     * Checks if a {@link ClassRoomEntity} is assigned to this course.
     * <p>
     * This method returns true if a {@link ClassRoomEntity} is currently assigned to this course, indicating
     * that the course is associated with a specific class. It returns false if no class is currently assigned.
     *
     * @return {@code true} if a {@link ClassRoomEntity} is assigned, false otherwise.
     */
    public boolean hasClassRoomAssigned()
    {
        return getClassRoom().isPresent();
    }

    /**
     * Retrieves a set of users associated with this course, including users from the assigned class, if present.
     * <p>
     * This method combines the local {@code users} with users from the assigned class (if available) using a
     * {@link Stream} of {@link UserEntity}. The resulting set is made unmodifiable.
     *
     * @return An unmodifiable set of {@link UserEntity} representing the users associated with this course.
     */
    public @NotNull @Unmodifiable Set<UserEntity> getUsers()
    {
        // add users from class if class is present
        Stream<UserEntity> userStream = getClassRoom().stream().flatMap(clazz -> clazz.getUsers().stream());
        return Stream.concat(users.stream(), userStream).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Saves this entity if the predicate returns true.
     * <p>
     * This method executes an action inside the predicate and saves this in the repository when the action was successfully
     * performed.
     * <p>
     * Note that this method uses the {@link CourseService} to persist changes, bypassing direct interaction
     * with the repository.
     *
     * @param courseService the service that is used to save this entity.
     * @param test          the entity that is given into the predicate.
     * @param predicate     the predicate that is used to test whether this entity should be saved.
     * @param <T>           the type of the test entity and the predicate
     * @return whether this entity has been saved.
     */
    private <T> boolean saveEntityIfPredicateTrue(@NotNull CourseService courseService, @NotNull T test, @NotNull Predicate<T> predicate)
    {
        if (predicate.test(test))
        {
            courseService.saveEntity(this);
            return true;
        }
        return false;
    }

    @Contract(pure = true) @Override public @NotNull String toString()
    { // Automatically generated by IntelliJ
        return "CourseEntity{" + "id=" + id + ", name='" + name + '\'' + ", users=" + users + ", subject=" + subject + '}';
    }

    @Override public boolean equals(Object o)
    { // Automatically generated by IntelliJ
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        CourseEntity that = (CourseEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override public int hashCode()
    { // Automatically generated by IntelliJ
        return Objects.hashCode(getId());
    }
}
