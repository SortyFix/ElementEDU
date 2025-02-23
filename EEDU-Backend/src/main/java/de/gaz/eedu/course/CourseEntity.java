package de.gaz.eedu.course;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.gaz.eedu.course.appointment.entry.AppointmentEntryEntity;
import de.gaz.eedu.course.appointment.entry.model.AppointmentEntryModel;
import de.gaz.eedu.course.appointment.frequent.FrequentAppointmentEntity;
import de.gaz.eedu.course.appointment.frequent.model.FrequentAppointmentModel;
import de.gaz.eedu.course.classroom.ClassRoomEntity;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.course.subject.SubjectEntity;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.file.FileEntity;
import de.gaz.eedu.user.AccountType;
import de.gaz.eedu.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a course entity in the system.
 * <p>
 * This class defines the structure of a course within the application, encapsulating information
 * such as the course id, associated users, and the subject to which the course is related.
 *
 * @author ivo
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "course_entity")
public class CourseEntity implements EntityModelRelation<Long, CourseModel>
{
    @ManyToMany @JsonManagedReference
    @JoinTable(
            name = "course_users", joinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private final Set<UserEntity> users = new HashSet<>();
    @OneToMany(mappedBy = "course", orphanRemoval = true) @JsonManagedReference @Getter(AccessLevel.NONE)
    private final Set<FrequentAppointmentEntity> frequentAppointments = new HashSet<>();
    @OneToMany(mappedBy = "course") @JsonManagedReference @Getter(AccessLevel.NONE)
    private final Set<AppointmentEntryEntity> appointments = new HashSet<>();
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(AccessLevel.NONE) private Long id; // ID is final
    private String name;
    @ManyToOne @JsonManagedReference @JoinColumn(name = "class_room_id", referencedColumnName = "id")
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE) private @Nullable ClassRoomEntity classRoom;
    @ManyToOne @JsonManagedReference @JoinColumn(name = "subject_id", referencedColumnName = "id")
    private SubjectEntity subject;
    @ManyToOne @JoinColumn(name = "repository_id", referencedColumnName = "id", unique = true)
    private FileEntity repository;

    public CourseEntity(@NotNull FileEntity repository)
    {
        this.repository = repository;
    }

    @Override public CourseModel toModel()
    {
        Stream<FrequentAppointmentEntity> scheduled = getFrequentAppointments().stream();

        return new CourseModel(
                getId(),
                getName(),
                getSubject().toModel(),
                Arrays.stream(getEntries()).map(AppointmentEntryEntity::toModel).toArray(AppointmentEntryModel[]::new),
                scheduled.map(FrequentAppointmentEntity::toModel).toArray(FrequentAppointmentModel[]::new),
                getClassRoom().map(ClassRoomEntity::toModel).orElse(null)
        );
    }

    public boolean setTeacher(@NotNull CourseService courseService, @NotNull UserEntity teacher)
    {
        return saveEntityIfPredicateTrue(courseService, teacher, this::setTeacher);
    }

    public boolean setTeacher(@NotNull UserEntity teacher)
    {
        this.users.removeIf(user -> Objects.equals(user.getAccountType(), AccountType.TEACHER));
        return this.users.add(teacher);
    }

    public void setSubject(@NotNull CourseService service, SubjectEntity subject)
    {
        this.setSubject(subject);
        service.saveEntity(this);
    }

    public boolean scheduleRepeating(@NotNull CourseService courseService, @NotNull FrequentAppointmentEntity... frequentAppointmentEntity)
    {
        return saveEntityIfPredicateTrue(courseService, frequentAppointmentEntity, this::scheduleRepeating);
    }

    public boolean scheduleRepeating(@NotNull FrequentAppointmentEntity... frequentAppointmentEntity)
    {
        Predicate<FrequentAppointmentEntity> notPart = current -> !Objects.equals(this, current.getCourse());
        return frequentAppointments.addAll(Arrays.stream(frequentAppointmentEntity).filter(notPart).toList());
    }

    public boolean unscheduleFrequent(@NotNull CourseService courseService, @NotNull Long... scheduledAppointmentEntity)
    {
        return saveEntityIfPredicateTrue(courseService, scheduledAppointmentEntity, this::unscheduleFrequent);
    }

    public boolean unscheduleFrequent(@NotNull Long... scheduledAppointmentEntity)
    {
        Set<Long> toRemove = Set.of(scheduledAppointmentEntity);
        return frequentAppointments.removeIf(appointment -> toRemove.contains(appointment.getId()));
    }

    public boolean setEntry(@NotNull CourseService service, @NotNull AppointmentEntryEntity entry)
    {
        if (setEntry(entry))
        {
            service.saveEntity(this);
            return true;
        }
        return false;
    }

    public boolean setEntry(@NotNull AppointmentEntryEntity entry)
    {
        return appointments.add(entry);
    }

    public @NotNull AppointmentEntryEntity[] getEntries()
    {
        return appointments.toArray(AppointmentEntryEntity[]::new);
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
     * @see #attachUsers(UserEntity...)
     */
    public boolean attachUsers(@NotNull CourseService courseService, @NonNull UserEntity... user)
    {
        return saveEntityIfPredicateTrue(courseService, user, this::attachUsers);
    }

    /**
     * Attaches the specified user entities to the current group, ensuring uniqueness.
     * <p>
     * This method attaches users to the course group, filtering out those already attached based on their equality.
     * It ensures that only unique, non-duplicate users are added.
     * <p>
     * Note that this method does not persist the changes.
     * In order for the changes to be permanent this object needs be saved which can be archived by {@link #attachUsers(CourseService, UserEntity...)},
     * or {@link CourseService#saveEntity(Iterable)}
     *
     * @param user The array of UserEntity instances to be attached to the group.
     * @return true if the users were successfully attached, false otherwise.
     * @see #attachUsers(CourseService, UserEntity...)
     * @see CourseService#saveEntity(Iterable)
     */
    public boolean attachUsers(@NonNull UserEntity... user)
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
     * @see #detachUsers(Long...)
     */
    public boolean detachUsers(@NotNull CourseService courseService, @NonNull Long... ids)
    {
        return saveEntityIfPredicateTrue(courseService, ids, this::detachUsers);
    }

    /**
     * Detaches the user entities with the specified IDs from the current group.
     * <p>
     * This method detaches users from this course based on their IDs.
     * Note that only users which are part of this course are removed.
     * <p>
     * To make the changes permanent, the object needs to be saved. This can be achieved by calling {@link #detachUsers(CourseService, Long...)}
     * or {@link CourseService#saveEntity(Iterable)} after detaching the users.
     *
     * @param ids The array of IDs corresponding to {@link UserEntity} instances to be detached from the group.
     * @return true if the users were successfully detached, false otherwise.
     * @see #detachUsers(Long...)
     * @see CourseService#saveEntity(Iterable)
     */
    public boolean detachUsers(@NonNull Long... ids)
    {
        List<Long> detachGroupIds = Arrays.asList(ids);
        return this.users.removeIf(groupEntity -> detachGroupIds.contains(groupEntity.getId()));
    }

    /**
     * Assigns a {@link ClassRoomEntity} to this course and saves the changes using the provided {@link CourseService}..
     * <p>
     * This method adds a {@link ClassRoomEntity} to the current course. It combines the users from the
     * {@link ClassRoomEntity#getStudents()} with the local {@code users}, accessible through {@link #getUsers()}.
     * <p>
     * It's important to note that this method uses the {@link CourseService} to persist changes.
     *
     * @param courseService The {@link CourseService} used to save the changes.
     * @param classRoom     The {@link ClassRoomEntity} to be associated with this course.
     * @return {@code true} if the association was successful, and changes were saved; false otherwise.
     */
    public boolean linkClassRoom(@NotNull CourseService courseService, @NotNull ClassRoomEntity classRoom)
    {
        return saveEntityIfPredicateTrue(courseService, classRoom, this::linkClassRoom);
    }

    /**
     * Assigns a {@link ClassRoomEntity} to this course.
     * <p>
     * This method adds a {@link ClassRoomEntity} to the current course. It combines the users from the
     * {@link ClassRoomEntity#getStudents()} with the local {@code users}, accessible through {@link #getUsers()}.
     *
     * @param classRoom The {@link ClassRoomEntity} to be associated with this course.
     * @return {@code true} if the association was successful, and changes were saved; false otherwise.
     */
    public boolean linkClassRoom(@NotNull ClassRoomEntity classRoom)
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
     * This method calls {@link #unlinkClassRoom()} to remove the association between the course and its assigned classroom.
     * The disassociation is persisted using the provided {@link CourseService}.
     *
     * @param courseService The {@link CourseService} used to save the changes.
     * @return {@code true} if the disassociation was successful, and changes were saved; false otherwise.
     */
    public boolean unlinkClassRoom(@NotNull CourseService courseService)
    {
        // That's what I call an API stretch
        return saveEntityIfPredicateTrue(courseService, unlinkClassRoom(), (value) -> value);
    }

    /**
     * Disassociates the currently assigned {@link ClassRoomEntity} from this course.
     * <p>
     * This method removes the association between the course and its assigned classroom by setting the
     * {@code classRoom} property to {@code null}.
     *
     * @return {@code true} if the disassociation was successful; false otherwise.
     */
    public boolean unlinkClassRoom()
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
        Stream<UserEntity> userStream = getClassRoom().stream().flatMap(clazz -> clazz.getStudents().stream());
        return Stream.concat(users.stream(), userStream).collect(Collectors.toUnmodifiableSet());
    }

    public @NotNull @Unmodifiable Set<FrequentAppointmentEntity> getFrequentAppointments()
    {
        return Collections.unmodifiableSet(frequentAppointments);
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
    private <T> boolean saveEntityIfPredicateTrue(@NotNull CourseService courseService, @Nullable T test, @NotNull Predicate<T> predicate)
    {
        if (predicate.test(test))
        {
            courseService.saveEntity(this);
            return true;
        }
        return false;
    }

    @Override public boolean deleteManagedRelations()
    {
        if (this.users.isEmpty())
        {
            return false;
        }
        this.users.clear();
        return true;
    }

    @Contract(pure = true, value = "-> new")
    @Override public String toString()
    { // Automatically generated by IntelliJ
        return "CourseEntity{" +
                "id=" + id +
                ", id='" + name + '\'' +
                '}';
    }

    @Override public boolean equals(Object o)
    { // Automatically generated by IntelliJ
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        CourseEntity that = (CourseEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override public int hashCode()
    { // Automatically generated by IntelliJ
        return Objects.hashCode(getId());
    }
}
