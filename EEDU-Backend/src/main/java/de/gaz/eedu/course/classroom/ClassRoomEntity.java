package de.gaz.eedu.course.classroom;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.classroom.model.ClassRoomModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.model.UserModel;
import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents a classroom entity in the database.
 *
 * @author ivo
 * @see EntityModelRelation
 * @see ClassRoomModel
 */
@Entity @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Table(name = "class_room_entity")
public class ClassRoomEntity implements EntityModelRelation<ClassRoomModel>
{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(AccessLevel.NONE) private Long id;
    private String name;

    @OneToMany(mappedBy = "classRoom") @JsonManagedReference
    private final Set<UserEntity> users = new HashSet<>();
    @OneToMany(mappedBy = "classRoom") @JsonBackReference private final Set<CourseEntity> courses = new HashSet<>();

    @Override public ClassRoomModel toModel()
    {
        //TODO
        return new ClassRoomModel(getId(), getName(), new UserModel[0], new CourseModel[0]);
    }

    /**
     * Retrieves the tutor for the current classroom.
     * <p>
     * This method returns an {@link Optional} containing the tutor for the classroom if one exists.
     *
     * @return an {@link Optional} containing the tutor for the classroom if one exists.
     */
    public @NotNull Optional<UserEntity> getTutor()
    {
        return getStudents().stream().filter(teacherPredicate(true)).findFirst();
    }

    /**
     * Sets the specified user entity as the tutor for the current classroom, modifying the current tutor if it exists.
     * <p>
     * This method sets the provided user as the tutor for the classroom. It ensures that the user has the required "teacher" role.
     * If the classroom already has a tutor, it will be overridden by the new tutor. The process is facilitated through the provided
     * {@link ClassRoomService} instance, allowing for the saving of changes.
     *
     * @param classRoomService the {@link ClassRoomService} instance to be used for persisting changes if needed.
     * @param userEntity       the {@link UserEntity} instance representing the tutor to be set for the classroom.
     * @return true if the tutor was successfully set, false otherwise.
     * @throws ResponseStatusException if the specified user lacks the "teacher" role.
     * @see #setTutor(UserEntity)
     */
    public boolean setTutor(@NotNull ClassRoomService classRoomService, @NotNull UserEntity userEntity)
    {
        return saveEntityIfPredicateTrue(classRoomService, userEntity, this::setTutor);
    }

    /**
     * Sets the specified user entity as the tutor for the current classroom, ensuring the user has the "teacher" role.
     * <p>
     * This method sets the provided user as the tutor for the classroom. It ensures that the user has the required "teacher" role.
     * If the classroom already has a tutor, it will be overridden by the new tutor. This method does not persist the changes.
     * In order for the changes to be permanent, this object needs to be saved, which can be achieved by
     * {@link #setTutor(ClassRoomService, UserEntity)}, or {@link ClassRoomService#saveEntity(EntityModelRelation)} after calling this method.
     *
     * @param userEntity the {@link UserEntity} instance representing the tutor to be set for the classroom.
     * @return true if the tutor was successfully set, false otherwise.
     * @throws ResponseStatusException if the specified user lacks the "teacher" role.
     * @see #setTutor(ClassRoomService, UserEntity)
     * @see ClassRoomService#saveEntity(EntityModelRelation)
     */
    public boolean setTutor(@NotNull UserEntity userEntity) throws ResponseStatusException
    {
        // TODO think about what would happen if user looses role
        if (!userEntity.hasRole("teacher"))
        {
            String errorMessage = "The specified user lacks the teacher role.";
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, errorMessage, new IllegalArgumentException());
        }

        if (this.users.removeIf(teacherPredicate(true)))
        {
            String logMessage = "The tutor from classroom {} has been overridden.";
            LoggerFactory.getLogger(ClassRoomEntity.class).info(logMessage, getId());
        }

        return this.users.add(userEntity);
    }

    /**
     * Unsets the tutor for the current classroom, using the provided {@link ClassRoomService} instance to persist changes.
     *
     * @param classRoomService the {@link ClassRoomService} instance to be used for persisting changes if needed.
     * @return true if the tutor was successfully unset, false otherwise.
     * @see #unsetTutor()
     */
    public boolean unsetTutor(@NotNull ClassRoomService classRoomService)
    {
        return saveEntityIfPredicateTrue(classRoomService, unsetTutor(), value -> value);
    }

    /**
     * Unsets the tutor for the current classroom.
     * <p>
     * This method removes the tutor from the classroom. This method does not persist the changes.
     * In order for the changes to be permanent, this object needs to be saved, which can be achieved by
     * {@link #unsetTutor(ClassRoomService)}, or {@link ClassRoomService#saveEntity(EntityModelRelation)} after calling this method.
     *
     * @return true if the tutor was successfully unset, false otherwise.
     * @see #unsetTutor(ClassRoomService)
     * @see ClassRoomService#saveEntity(EntityModelRelation)
     */
    public boolean unsetTutor()
    {
        return this.users.removeIf(teacherPredicate(true));
    }

    /**
     * Attaches the specified student entities to the current classroom.
     * <p>
     * This method attaches students to the current classroom.
     * The attachment process is facilitated through the provided {@link ClassRoomService} instance,
     * allowing for the saving of changes.
     *
     * @param classRoomService the {@link ClassRoomService} instance to be used for persisting changes if needed.
     * @param user             the array of {@link UserEntity} instances representing students to be attached to the classroom.
     * @return true if any students were attached, false otherwise.
     * @see #attachStudents(UserEntity...)
     */
    public boolean attachStudents(@NotNull ClassRoomService classRoomService, @NonNull UserEntity... user)
    {
        return saveEntityIfPredicateTrue(classRoomService, user, this::attachStudents);
    }

    /**
     * Attaches the specified student entities to the current classroom, ensuring uniqueness.
     * <p>
     * This method attaches students to the classroom, filtering out those already attached based on their equality.
     * It ensures that only unique, non-duplicate students are added.
     * <p>
     * Note that this method does not persist the changes.
     * In order for the changes to be permanent, this object needs to be saved, which can be achieved by
     * {@link #attachStudents(ClassRoomService, UserEntity...)}, or {@link ClassRoomService#saveEntity(EntityModelRelation)} after calling this method
     *
     * @param user The array of {@link UserEntity} instances representing students to be attached to the classroom.
     * @return true if any students were attached, false otherwise.
     * @see #attachStudents(ClassRoomService, UserEntity...)
     * @see ClassRoomService#saveEntity(EntityModelRelation)
     */
    public boolean attachStudents(@NonNull UserEntity... user)
    {
        return this.users.addAll(Arrays.stream(user).filter(teacherPredicate(false)).collect(Collectors.toSet()));
    }

    /**
     * Detaches students with the specified IDs from the current classroom.
     * <p>
     * This method detaches students from the current classroom based on their IDs. I
     * The detachment process is facilitated through the provided {@link ClassRoomService} instance, allowing for the saving of changes.
     *
     * @param classRoomService the {@link ClassRoomService} instance to be used for persisting changes if needed.
     * @param ids              the array of student IDs to be detached from the classroom.
     * @return true if any students were detached, false otherwise.
     * @see #detachStudents(Long...)
     */
    public boolean detachStudents(@NotNull ClassRoomService classRoomService, @NonNull Long... ids)
    {
        return saveEntityIfPredicateTrue(classRoomService, ids, this::detachStudents);
    }

    /**
     * Detaches students with the specified IDs from the current classroom.
     * <p>
     * This method detaches students from the classroom based on their IDs, filtering out those who are not currently attached.
     * It ensures that only existing students are detached.
     * <p>
     * Note that this method does not persist the changes.
     * In order for the changes to be permanent, this object needs to be saved, which can be achieved by
     * {@link #detachStudents(ClassRoomService, Long...)}, or {@link ClassRoomService#saveEntity(EntityModelRelation)} after calling this method
     *
     * @param ids The array of student IDs to be detached from the classroom.
     * @return true if any students were detached, false otherwise.
     * @see #detachStudents(ClassRoomService, Long...)
     * @see ClassRoomService#saveEntity(EntityModelRelation)
     */
    public boolean detachStudents(@NonNull Long... ids)
    {
        List<Long> detachGroupIds = Arrays.asList(ids);
        return this.users.removeIf(user -> detachGroupIds.contains(user.getId()));
    }

    public boolean detachStudents(@NotNull ClassRoomService classRoomService)
    {
        return saveEntityIfPredicateTrue(classRoomService, detachStudents(), value -> value);
    }

    public boolean detachStudents()
    {
        return users.removeIf(teacherPredicate(false));
    }

    /**
     * Retrieves an unmodifiable set of students for the current classroom.
     * <p>
     * This method returns an unmodifiable {@link Set} containing all the students associated with the classroom.
     *
     * @return an unmodifiable {@link Set} containing all the students associated with the classroom.
     */
    public @NotNull @Unmodifiable Set<UserEntity> getStudents()
    {
        return users.stream().filter(teacherPredicate(false)).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Saves the provided entity in the database if the given predicate evaluates to true.
     * <p>
     * This method checks the specified predicate on the provided entity and saves it using the provided
     * ClassRoomService if the predicate evaluates to true. Otherwise, it does not save the entity.
     *
     * @param classRoomService the service responsible for saving the entity.
     * @param test             the entity to be saved.
     * @param predicate        the condition that, if true, triggers the entity to be saved.
     * @param <T>              the type of the entity.
     * @return true if the entity was saved, false otherwise.
     */
    private <T> boolean saveEntityIfPredicateTrue(
            @NotNull ClassRoomService classRoomService, @NotNull T test, @NotNull Predicate<T> predicate)
    {
        if (predicate.test(test))
        {
            classRoomService.saveEntity(this);
            return true;
        }
        return false;
    }

    @Override public boolean deleteManagedRelations()
    {
        if(this.users.isEmpty())
        {
            return false;
        }
        this.users.clear();
        return true;
    }

    /**
     * Generates a {@code Predicate<UserEntity>} based on the specified role condition.
     * <p>
     * This method creates a Predicate<UserEntity> based on the specified role condition (teacher or not).
     * The generated predicate can be used to filter UserEntity objects based on their role.
     *
     * @param teacher true if the generated predicate should check for the "teacher" role, false otherwise.
     * @return a Predicate<UserEntity> based on the specified role condition.
     */
    @Contract(pure = true, value = "_ -> new") private @NotNull Predicate<UserEntity> teacherPredicate(boolean teacher)
    {
        return current -> (teacher == current.hasRole("teacher"));
    }

    @Contract(pure = true) @Override public @NotNull String toString()
    { // Automatically generated by IntelliJ
        return "ClassRoomEntity{" + "id=" + id + ", name='" + name + '\'' + ", users=" + users + ", courses=" + courses + '}';
    }

    @Override public boolean equals(Object o)
    { // Automatically generated by IntelliJ
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        ClassRoomEntity that = (ClassRoomEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override public int hashCode()
    { // Automatically generated by IntelliJ
        return Objects.hashCode(getId());
    }
}
