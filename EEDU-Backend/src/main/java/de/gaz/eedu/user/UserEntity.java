package de.gaz.eedu.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.classroom.ClassRoomEntity;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.model.GroupModel;
import de.gaz.eedu.user.illnessnotifications.IllnessNotificationEntity;
import de.gaz.eedu.user.model.ReducedUserModel;
import de.gaz.eedu.user.model.UserModel;
import de.gaz.eedu.user.theming.ThemeEntity;
import de.gaz.eedu.user.verification.credentials.CredentialEntity;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class represents the user.
 * <p>
 * In this specific class the user is represented as an object.
 * It's noteworthy that this class is connected to a table in a database using jakarta.persistence.
 * <p>
 * This {@link UserEntity} can be a part of many {@link GroupEntity}s. These can have
 * {@link de.gaz.eedu.user.privileges.PrivilegeEntity}s.
 * This is important when authorizing this user as {@link de.gaz.eedu.user.privileges.PrivilegeEntity} cannot be
 * directly added to a users account.
 *
 * @author ivo
 * @see GroupEntity
 * @see de.gaz.eedu.user.privileges.PrivilegeEntity
 */
@Entity @Table(name = "user_entity")
@Slf4j @NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class UserEntity implements UserDetails, EntityModelRelation<Long, UserModel>
{
    @Enumerated UserStatus status;
    //finish this line and the sql
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) @JsonManagedReference
    List<IllnessNotificationEntity> illnessNotificationEntities = new ArrayList<>();

    // id
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(AccessLevel.NONE) private Long id; // ID is final

    // user data
    private boolean systemAccount;
    @Enumerated private AccountType accountType;
    private String firstName, lastName, loginName;
    private boolean enabled, locked;

    // credentials
    @JsonManagedReference @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CredentialEntity> credentials = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "theme_id") @JsonManagedReference
    private ThemeEntity themeEntity;

    // groups
    @JsonManagedReference @ManyToMany @JoinTable(
            name = "user_groups", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id")
    ) @Setter(AccessLevel.PRIVATE) @Getter(AccessLevel.NONE) private Set<GroupEntity> groups = new HashSet<>();

    // courses
    @JsonBackReference @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
    @Getter(AccessLevel.NONE) private final Set<CourseEntity> courses = new HashSet<>();

    // classroom
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "class_room_id") @JsonManagedReference
    @Setter(AccessLevel.NONE) @Getter(AccessLevel.NONE) private @Nullable ClassRoomEntity classRoom;

    // Transient is not saved inside the database
    @Transient @Getter(AccessLevel.PROTECTED) private GroupEntity typeGroup;

    public void setTypeGroup(@NotNull GroupEntity typeGroup)
    {
        if(this.typeGroup == typeGroup)
        {
            return;
        }
        this.typeGroup = typeGroup;
    }

    @Override public String getPassword()
    {
        throw new UnsupportedOperationException("Users can have multiple passwords");
    }

    @Override public boolean isDeletable()
    {
        return !systemAccount;
    }

    @Override public UserModel toModel()
    {
        GroupModel[] groups = getGroups().stream().map(GroupEntity::toModel).toArray(GroupModel[]::new);
        return new UserModel(
                getId(),
                getFirstName(),
                getLastName(),
                getLoginName(),
                getAccountType(),
                getStatus(),
                groups,
                getThemeEntity().toModel(),
                getClassRoom().map(ClassRoomEntity::toModel).orElse(null)
        );
    }

    public @NotNull ReducedUserModel toReducedModel()
    {
        return new ReducedUserModel(this.getId(), this.getFirstName(), this.getLastName(), this.getAccountType());
    }

    @Override public Set<? extends GrantedAuthority> getAuthorities()
    {
        return getGroups().stream().flatMap(groupEntity ->
        {
            Set<GrantedAuthority> authorities = groupEntity.toSpringSecurity();
            return authorities.stream();
        }).collect(Collectors.toUnmodifiableSet());
    }

    @Override public @NotNull String getUsername()
    {
        return loginName;
    }

    @Override public boolean isAccountNonLocked()
    {
        return !isLocked();
    }

    @Override public boolean isEnabled()
    {
        return enabled;
    }

    @Override public boolean isAccountNonExpired()
    {
        return false; // TODO maybe implement (low priority)
    }

    @Override public boolean isCredentialsNonExpired()
    {
        return false; // TODO maybe implement (low priority)
    }

    /**
     * Returns a full representing name.
     * <p>
     * Combines the {@code firstName} and the {@code lastName} to one string divided by a ,.
     * This can be used then to write texts using the full name of the student this object represents.
     *
     * @return the combined string which is never null as the two variables are neither.
     */
    public @NotNull String getFullName()
    {
        return getLastName() + ", " + getFirstName();
    }

    /**
     * Attaches various groups to the user and saves the state using the provided user service.
     * The groups are specified via their entities.
     * The method will update the user's associated groups and then use the service to save the updated entity state.
     * <p>
     * The process is conducted within a transaction to ensure that all changes are either applied in full or not at
     * all.
     * This is essential in maintaining data integrity.
     *
     * @param userService   The service used for saving the user entity after attaching the groups.
     * @param groupEntities The groups to be attached.
     * @return true if a group was successfully attached and the user entity was saved, false otherwise.
     */
    @Transactional public boolean attachGroups(@NotNull UserService userService, @NotNull GroupEntity... groupEntities)
    {
        return saveEntityIfPredicateTrue(userService, groupEntities, this::attachGroups);
    }

    /**
     * Attaches a {@link GroupEntity} to this user.
     * <p>
     * This method adds groups this user should be in. This is stored in {@code groups}.
     * These groups can be detached using {@link #detachGroups(Long...)}.
     * <p>
     * Note that this method accesses {@code groups} directly as the getter
     * {@link #getGroups()} returns an {@link Unmodifiable} list.
     *
     * @param groupEntities to add to the user.
     * @return whether a group has been added or not.
     * @see #detachGroups(Long...)
     * @see #getGroups()
     */
    public boolean attachGroups(@NotNull GroupEntity... groupEntities)
    {
        List<GroupEntity> entities = Arrays.asList(groupEntities);
        if(!Collections.disjoint(this.groups, entities))
        {
            throw new IllegalStateException("The user already has one of these groups.");
        }

        return this.groups.addAll(entities);
    }

    /**
     * Detaches various groups from the user and saves the state using the provided user service.
     * The groups are specified by their IDs.
     * The method will update the user's associated groups and then use the service to save the updated entity state.
     * <p>
     * The process is carried out within a transaction to ensure that all changes are either committed completely or
     * not at all.
     * This is critical for maintaining data integrity.
     *
     * @param userService The service used for saving the user entity after detaching the groups.
     * @param ids         The IDs of the groups to be detached.
     * @return true if a group was successfully detached and the user entity was saved, false otherwise.
     */
    public boolean detachGroups(@NotNull UserService userService, @NotNull Long... ids)
    {
        return saveEntityIfPredicateTrue(userService, ids, this::detachGroups);
    }

    /**
     * Detaches {@link GroupEntity}s from this user.
     * <p>
     * This method revokes a relation with a {@link GroupEntity} stored in {@code groups}.
     * <p>
     * Note that this method access {@code groups} directly as the getter {@link #getGroups()}
     * returns a {@link Unmodifiable} list.
     *
     * @param ids the ids to remove
     * @return whether a group has been deleted, or not.
     * @see #attachGroups(GroupEntity...)
     * @see #getGroups()
     */
    public boolean detachGroups(@NotNull Long... ids)
    {
        List<Long> detachGroupIds = Arrays.asList(ids);
        return this.groups.removeIf(groupEntity -> detachGroupIds.contains(groupEntity.getId()));
    }

    /**
     * Returns the content of {@code groups}.
     * <p>
     * This method returns the contents of the list {@code groups} and makes it {@link Unmodifiable}.
     * This is necessary to clarify that the list should not be edited this way but rather using the method
     * {@link #attachGroups(GroupEntity...)}
     * or {@link #detachGroups(Long...)}.
     * <p>
     * Update 10.02.2025:
     * Note that {@link #getTypeGroup()} will get added to the list.
     *
     * @return an unmodifiable list.
     * @see #attachGroups(GroupEntity...)
     * @see #detachGroups(Long...)
     */
    public @NotNull @Unmodifiable Set<GroupEntity> getGroups()
    {
        if(Objects.isNull(getTypeGroup()))
        {
            throw new IllegalStateException("The type group was not yet set.");
        }

        return Stream.concat(groups.stream(), Stream.of(getTypeGroup())).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Checks if the user is a member of a group with the specified name.
     * <p>
     * This method searches for a {@link GroupEntity} in the user's groups with a matching name. Returns {@code true}
     * if a matching group is found, indicating the user's membership in that group; otherwise, returns {@code false}.
     *
     * @param name The name of the group to check for membership.
     * @return {@code true} if the user is a member of a group with the specified name; {@code false} otherwise.
     */
    public boolean inGroup(@NotNull String name)
    {
        return getGroups().stream().anyMatch(groupEntity -> groupEntity.getName().equals(name));
    }

    /**
     * Checks if the user has a role with the specified name.
     * <p>
     * This method checks if the user has the specified role by converting it to the corresponding authority
     * with the "ROLE_" prefix. Returns {@code true} if the user has the specified role; otherwise, returns {@code false}.
     *
     * @param role The name of the role to check.
     * @return {@code true} if the user has the specified role; {@code false} otherwise.
     */
    public boolean hasRole(@NotNull String role)
    {
        return hasAuthority("ROLE_" + role);
    }

    public boolean hasAuthority(@NotNull String name)
    {
        return getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(name.toUpperCase()));
    }

    public boolean hasAnyRole(@NotNull Collection<String> roles)
    {
        return hasAnyAuthority(roles.stream().map(role -> "ROLE_" + role).collect(Collectors.toSet()));
    }

    public boolean hasAnyAuthority(@NotNull Collection<String> authorities)
    {
        Set<String> loadedAuthorities = getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        return authorities.stream().anyMatch(loadedAuthorities::contains);
    }

    public @NotNull Set<CredentialEntity> getCredentials(@NotNull CredentialMethod credentialMethod)
    {
        // Also include not enabled ones, therefore not getter
        return credentials.stream().filter(entity -> entity.getMethod().equals(credentialMethod)).collect(Collectors.toUnmodifiableSet());
    }

    @Transactional
    public boolean initCredential(@NotNull UserService userService, @NotNull CredentialEntity credentialEntity)
    {
        return saveEntityIfPredicateTrue(userService, credentialEntity, this::initCredential);
    }

    public boolean initCredential(@NotNull CredentialEntity credentialEntity)
    {
        return this.credentials.add(credentialEntity);
    }

    @Transactional public boolean disableCredential(@NotNull UserService userService, @NotNull Long... ids)
    {
        return saveEntityIfPredicateTrue(userService, ids, this::disableCredential);
    }

    public boolean disableCredential(@NotNull Long... ids)
    {
        Set<Long> disableFactors = Set.of(ids);
        return credentials.removeIf(currentEntity -> disableFactors.contains(currentEntity.getId()));
    }

    public @NotNull @Unmodifiable Set<CredentialEntity> getCredentials()
    {
        return credentials.stream().filter(CredentialEntity::isEnabled).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Retrieves an unmodifiable set of {@link CourseEntity} instances associated with this course.
     * <p>
     * This method combines the courses directly associated with this course and those associated with its assigned
     * {@link ClassRoomEntity}, if present. The resulting set is unmodifiable.
     *
     * @return An unmodifiable set of {@link CourseEntity} instances associated with this course.
     */
    public @NotNull @Unmodifiable Set<CourseEntity> getCourses()
    {
        // add courses from class if class is present
        Stream<CourseEntity> courseStream = getClassRoom().stream().flatMap(clazz -> clazz.getCourses().stream());
        return Stream.concat(this.courses.stream(), courseStream).collect(Collectors.toUnmodifiableSet());
    }

    public boolean setClassRoom(@NotNull UserService userService, @Nullable ClassRoomEntity classRoom)
    {
        return this.saveEntityIfPredicateTrue(userService, classRoom, this::setClassRoom);
    }

    public boolean setClassRoom(@Nullable ClassRoomEntity classRoom)
    {
        this.classRoom = classRoom;
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
     * Checks if a {@link ClassRoomEntity} is assigned to this user.
     * <p>
     * This method returns true if a {@link ClassRoomEntity} is currently assigned to this user, indicating
     * that the user is associated with a specific class. It returns false if no class is currently assigned.
     * <p>
     * Note: To assign a {@link ClassRoomEntity} to this course, use the {@link #setClassRoom(ClassRoomEntity)} method.
     *
     * @return {@code true} if a {@link ClassRoomEntity} is assigned, false otherwise.
     * @see #setClassRoom(ClassRoomEntity)
     * @see #setClassRoom(UserService, ClassRoomEntity)
     */
    public boolean hasClassRoomAssigned()
    {
        return getClassRoom().isPresent();
    }

    @Transactional public void setThemeEntity(@NotNull UserService userService, @NotNull ThemeEntity themeEntity)
    {
        setThemeEntity(themeEntity);
        userService.saveEntity(this);
    }

    @Override public boolean deleteManagedRelations()
    {
        if (this.groups.isEmpty())
        {
            return false;
        }
        this.groups.clear();
        return true;
    }

    @Contract(pure = true, value = "-> new")
    @Override public String toString()
    { // Automatically generated by IntelliJ
        return "UserEntity{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                '}';
    }

    @Override public boolean equals(Object object)
    { // Automatically generated by IntelliJ
        if (this == object) {return true;}
        if (object == null || getClass() != object.getClass()) {return false;}
        UserEntity userEntity = (UserEntity) object;
        return Objects.equals(getId(), userEntity.getId());
    }

    @Override public int hashCode()
    { // Automatically generated by IntelliJ
        return Objects.hash(getId());
    }

    private <T> boolean saveEntityIfPredicateTrue(@NotNull UserService userService, @Nullable T entity, @NotNull Predicate<T> predicate)
    {
        if (predicate.test(entity))
        {
            userService.saveEntity(this);
            return true;
        }
        return false;
    }
}
