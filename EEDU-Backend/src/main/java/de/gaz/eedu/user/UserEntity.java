package de.gaz.eedu.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.model.SimpleUserGroupModel;
import de.gaz.eedu.user.illnessnotifications.IllnessNotificationEntity;
import de.gaz.eedu.user.model.SimpleUserModel;
import de.gaz.eedu.user.model.UserModel;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import de.gaz.eedu.user.privileges.model.SimplePrivilegeModel;
import de.gaz.eedu.user.theming.ThemeEntity;
import de.gaz.eedu.user.verfication.twofa.TwoFactorEntity;
import de.gaz.eedu.user.verfication.twofa.implementations.TwoFactorMethod;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
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
@Entity @Getter @AllArgsConstructor @NoArgsConstructor @Setter @Table(name = "user_entity") public class UserEntity implements UserDetails, EntityModelRelation<UserModel>
{
    private final static Logger LOGGER = LoggerFactory.getLogger(UserEntity.class);
    @Enumerated UserStatus status;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(AccessLevel.NONE) private Long id; // ID is final
    private String firstName, lastName, loginName, password;
    private boolean enabled, locked;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) @JsonManagedReference private Set<TwoFactorEntity> twoFactors = new HashSet<>();
    @ManyToOne @JoinColumn(name = "theme_id") @JsonManagedReference private ThemeEntity themeEntity;
    @ManyToMany @JsonManagedReference @Setter(AccessLevel.PRIVATE) @JoinTable(name = "user_groups", joinColumns =
    @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "group_id",
            referencedColumnName = "id")) private Set<GroupEntity> groups = new HashSet<>();
    //finish this line and the sql
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) @JsonManagedReference List<IllnessNotificationEntity> illnessNotificationEntities = new ArrayList<>();

    @ManyToMany(mappedBy = "users")
    @JsonBackReference
    @Setter(AccessLevel.NONE)
    private Set<CourseEntity> courses = new HashSet<>();

    public @NotNull SimpleUserModel toSimpleModel()
    {
        return new SimpleUserModel(getId(),
                getFirstName(),
                getLastName(),
                getLoginName(),
                isEnabled(),
                isLocked(),
                getThemeEntity().toSimpleModel(),
                getStatus());
    }

    @Override public UserModel toModel()
    {
        Function<GroupEntity, SimpleUserGroupModel> mapper = (entity) ->
        {
            SimplePrivilegeModel[] privilegeModels = entity.getPrivileges()
                    .stream()
                    .map(PrivilegeEntity::toSimpleModel)
                    .toArray(SimplePrivilegeModel[]::new);
            return new SimpleUserGroupModel(entity.getId(), entity.getName(), entity.isTwoFactorRequired(), privilegeModels);
        };

        return new UserModel(getId(),
                getFirstName(),
                getLastName(),
                getLoginName(),
                isEnabled(),
                isLocked(),
                getTwoFactors().stream().map(TwoFactorEntity::toModel).distinct().toArray(TwoFactorModel[]::new),
                getThemeEntity().toSimpleModel(),
                getGroups().stream().map(mapper).toArray(SimpleUserGroupModel[]::new),
                getStatus());
    }

    @Override public Set<? extends GrantedAuthority> getAuthorities()
    {
        Function<GroupEntity, Stream<GrantedAuthority>> flat = groupEntity -> groupEntity.toSpringSecurity().stream();
        return groups.stream().flatMap(flat).collect(Collectors.toSet());
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
        // Filter already attached groups out
        Predicate<GroupEntity> predicate = requestedGroup -> getGroups().stream()
                .noneMatch(presentGroup -> Objects.equals(presentGroup, requestedGroup));
        return this.groups.addAll(Arrays.stream(groupEntities).filter(predicate).collect(Collectors.toSet()));
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
    @Transactional public boolean detachGroups(@NotNull UserService userService, @NotNull Long... ids)
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
     *
     * @return an unmodifiable list.
     * @see #attachGroups(GroupEntity...)
     * @see #detachGroups(Long...)
     */
    public @NotNull @Unmodifiable Set<GroupEntity> getGroups()
    {
        return Collections.unmodifiableSet(groups);
    }

    public @NotNull @Unmodifiable Set<CourseEntity> getCourses()
    {
        return Collections.unmodifiableSet(courses);
    }

    public @NotNull Optional<TwoFactorEntity> getTwoFactor(@NotNull TwoFactorMethod twoFactorMethod)
    {
        // Also include not enabled ones, therefore not getter
        return twoFactors.stream().filter(entity -> entity.getMethod().equals(twoFactorMethod)).findFirst();
    }

    @Transactional public boolean initTwoFactor(@NotNull UserService userService,
		    @NotNull TwoFactorEntity twoFactorEntity)
    {
        return saveEntityIfPredicateTrue(userService, twoFactorEntity, this::initTwoFactor);
    }

    public boolean initTwoFactor(@NotNull TwoFactorEntity twoFactorEntity)
    {
        if (this.getTwoFactors().stream().anyMatch(entity -> entity.getMethod().equals(twoFactorEntity.getMethod())))
        {
            return false;
        }

        // remove not enabled instances of this two factor
        if (twoFactors.removeIf(entity -> Objects.equals(entity.getMethod(), twoFactorEntity.getMethod())))
        {
            String removalMessage = "A disabled two-factor instance using method {} was overridden from user {}.";
            LOGGER.warn(removalMessage, twoFactorEntity.getMethod(), getId());
        }

        return this.twoFactors.add(twoFactorEntity);
    }

    @Transactional public boolean disableTwoFactor(@NotNull UserService userService, @NotNull Long... ids)
    {
        return saveEntityIfPredicateTrue(userService, ids, this::disableTwoFactor);
    }

    public boolean disableTwoFactor(@NotNull Long... ids)
    {
        Set<Long> disableFactors = Stream.of(ids)
                .filter(current -> getTwoFactors().stream().anyMatch(entity -> Objects.equals(entity.getId(), current)))
                .collect(Collectors.toSet());
        return twoFactors.removeIf(currentEntity -> disableFactors.contains(currentEntity.getId()));
    }

    public @NotNull @Unmodifiable Set<TwoFactorEntity> getTwoFactors()
    {
        return twoFactors.stream().filter(TwoFactorEntity::isEnabled).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Checks whether this user is in the specified {@link CourseEntity}.
     * <p>
     * This method checks whether this user is part of the {@link CourseEntity} provided.
     * It does this by iterating over {@code getCourses()} and checks their ids.
     *
     * @param id the id of the course to check for.
     * @return whether this user is part of this course.
     */
    public boolean inCourse(long id)
    {
        //TODO add classes
        return getCourses().stream().anyMatch(course -> course.getId() == id);
    }

    @Transactional public void setThemeEntity(@NotNull @org.jetbrains.annotations.NotNull UserService userService,
		    @NotNull ThemeEntity themeEntity)
    {
        setThemeEntity(themeEntity);
        userService.saveEntity(this);
    }

    @Override public String toString()
    { // Automatically generated using intellij
        return "UserEntity{" + "id=" + id + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' +
		        ", password='" + password + '\'' + ", enabled=" + enabled + ", locked=" + locked;
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

    private <T> boolean saveEntityIfPredicateTrue(@NotNull UserService userService, @NotNull T entity,
		    @org.jetbrains.annotations.NotNull Predicate<T> predicate)
    {
        if (predicate.test(entity))
        {
            userService.saveEntity(this);
            return true;
        }
        return false;
    }
}
