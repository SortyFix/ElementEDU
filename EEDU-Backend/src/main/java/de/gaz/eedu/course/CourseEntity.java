package de.gaz.eedu.course;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.course.subjects.SubjectEntity;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.model.UserModel;
import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents a course entity in the system.
 * <p>
 * This class defines the structure of a course within the application, encapsulating information
 * such as the course name, associated users, and the subject to which the course is related.
 *
 * @author ivo
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "course_entity")
public class CourseEntity implements EntityModelRelation<CourseModel>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(AccessLevel.NONE) private Long id; // ID is final
    private String name;
    @ManyToMany
    @JsonManagedReference
    @JoinTable(name = "course_users", joinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private final Set<UserEntity> users = new HashSet<>();
    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "subject_id", referencedColumnName = "id")
    private SubjectEntity subject;

    @Override
    public CourseModel toModel()
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
     * @param user         the array of {@link UserEntity} instances to be attached to the group.
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
     * It does this by checking the ids of each user from {@code getUsers()}. TODO also add classes
     *
     * @param id the id of the user which should be checked
     * @return whether this user is in the course or not.
     */
    public boolean inCourse(long id)
    {
        return getUsers().stream().anyMatch(user -> Objects.equals(user.getId(), id));
    }

    /**
     * Saves this entity if the predicate returns true.
     * <p>
     * This method executes an action inside the predicate and saves this in the repository when the action was successfully
     * performed.
     * <p>
     * Note that this method uses the {@link CourseService} in order to save the changes instead of the actual repository.
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

    @Contract(pure = true)
    @Override
    public @NotNull String toString()
    { // Automatically generated by IntelliJ
        return "CourseEntity{" + "id=" + id + ", name='" + name + '\'' + ", users=" + users + ", subject=" + subject + '}';
    }

    @Override
    public boolean equals(Object o)
    { // Automatically generated by IntelliJ
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        CourseEntity that = (CourseEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode()
    { // Automatically generated by IntelliJ
        return Objects.hashCode(getId());
    }
}
