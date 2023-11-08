package de.gaz.eedu.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.model.SimpleUserGroupModel;
import de.gaz.eedu.user.model.SimpleUserModel;
import de.gaz.eedu.user.model.UserModel;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import de.gaz.eedu.user.theming.ThemeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(AccessLevel.NONE) private Long id; // ID is final
    private String firstName, lastName, loginName, password;
    private boolean enabled, locked;
    @ManyToOne @JoinColumn(name = "theme_id") @JsonManagedReference private ThemeEntity themeEntity;

    @ManyToMany @JsonManagedReference @Setter(AccessLevel.PRIVATE) @JoinTable(name = "user_groups", joinColumns =
    @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "group_id",
            referencedColumnName = "id")) private Set<GroupEntity> groups = new HashSet<>();

    public @NotNull SimpleUserModel toSimpleModel()
    {
        return new SimpleUserModel(getId(), getFirstName(), getLastName(), getLoginName(), isEnabled(), isLocked(), getThemeEntity());
    }

    @Override public UserModel toModel()
    {
        return new UserModel(getId(),
                getFirstName(),
                getLastName(),
                getLoginName(),
                isEnabled(),
                isLocked(),
                getThemeEntity(),
                getGroups().stream().map(groupEntity -> new SimpleUserGroupModel(groupEntity.getId(),
                        groupEntity.getName(),
                        groupEntity.getPrivileges().stream().map(PrivilegeEntity::toSimpleModel).collect(Collectors.toSet()))).collect(
                        Collectors.toSet()));
    }

    @Override public Set<? extends GrantedAuthority> getAuthorities()
    {
        return groups.stream().flatMap(groupEntity -> groupEntity.getPrivileges().stream().map(privilege -> new SimpleGrantedAuthority(
                privilege.getName()))).collect(Collectors.toSet());
    }

    @Override public @NotNull String getUsername()
    {
        return loginName;
    }

    @Override public boolean isAccountNonLocked()
    {
        return locked;
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
        return lastName + ", " + firstName;
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
        if (attachGroups(groupEntities))
        {
            userService.saveEntity(this);
            return true;
        }
        return false;
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
        Predicate<GroupEntity> predicate =
                requestedGroup -> this.groups.stream().noneMatch(presentGroup -> Objects.equals(
                        presentGroup,
                        requestedGroup));
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
        if (detachGroups(ids))
        {
            userService.saveEntity(this);
            return true;
        }
        return false;
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
}
