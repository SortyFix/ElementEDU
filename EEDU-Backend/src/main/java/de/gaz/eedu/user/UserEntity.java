package de.gaz.eedu.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.model.SimpleUserGroupModel;
import de.gaz.eedu.user.model.SimpleUserModel;
import de.gaz.eedu.user.model.UserModel;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

    @ManyToMany @JsonManagedReference @Setter(AccessLevel.PRIVATE) @JoinTable(name = "user_groups", joinColumns =
    @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "group_id",
            referencedColumnName = "id")) private Set<GroupEntity> groups;

    public @NotNull SimpleUserModel toSimpleModel()
    {
        return new SimpleUserModel(getId(), getFirstName(), getLastName(), getLoginName(), isEnabled(), isLocked());
    }

    @Override public UserModel toModel()
    {
        return new UserModel(getId(),
                getFirstName(),
                getLastName(),
                getLoginName(),
                isEnabled(),
                isLocked(),
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
                ", password='" + password + '\'' + ", enabled=" + enabled + ", locked=" + locked + ", groupEntities=" + groups + '}';
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
