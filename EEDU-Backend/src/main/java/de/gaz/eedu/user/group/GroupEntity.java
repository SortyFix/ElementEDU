package de.gaz.eedu.user.group;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class represents a database entry of a group.
 * <p>
 * This is the representation of a database entry of a group. It contains its id, name, userEntities and the privileges this group has.
 * Groups are used to cluster {@link UserEntity} together and manage their access precisely. This is archived by adding {@link PrivilegeEntity}s to
 * this object and then assign users to it.
 * <p>
 * Note that a {@link UserEntity} can be part of multiple groups.
 *
 * @author ivo
 * @see UserEntity
 * @see PrivilegeEntity
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "group_entity")
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "groups")
    @JsonBackReference
    private Set<UserEntity> users;
    @ManyToMany
    @JsonManagedReference
    @JoinTable(name = "group_privileges", joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    private Set<PrivilegeEntity> privileges;

    /**
     * Grants {@link PrivilegeEntity}s to this group.
     * <p>
     * This method can add multiple privileges to this group and therefore to all users being part of this group.
     * This uses an {@link Set#addAll(Collection)} and ignores privileges already added.
     * <p>
     * Note that this method accesses the {@code privilegeEntities} set directly as the getter {@link #getPrivileges()}
     * returns a {@link Unmodifiable} set.
     *
     * @param privilegeEntity which should be added to this method.
     * @return whether a {@link PrivilegeEntity} has been added to this method.
     * @see #revokePrivilege(Long...)
     * @see #getPrivileges()
     */
    public boolean grantPrivilege(@NotNull PrivilegeEntity... privilegeEntity) {
        // Filter already granted privileges out
        Predicate<PrivilegeEntity> privilegeEntityPredicate = requestedPrivilege -> privileges.stream().noneMatch(presentPrivilege -> Objects.equals(presentPrivilege, requestedPrivilege));
        return privileges.addAll(Arrays.stream(privilegeEntity).filter(privilegeEntityPredicate).collect(Collectors.toSet()));
    }

    /**
     * Revokes {@link PrivilegeEntity}s from this group.
     * <p>
     * This method removes privileges from this group and therefore from all users having this group.
     * This uses the {@link Set#removeIf(Predicate)} method which gets rid of duplicated entries if there was some kind of error.
     * <p>
     * Note that this method accesses the {@code privilegeEntities} set directly as the getter {@link #getPrivileges()} returns a {@link Unmodifiable} set.
     *
     * @param id of the {@link PrivilegeEntity} to remove from this group.
     * @return whether a privilege has been revoked from this group.
     * @see #grantPrivilege(PrivilegeEntity...)
     * @see #getPrivileges()
     */
    public boolean revokePrivilege(@NotNull Long... id) {
        Collection<Long> revokeIds = Arrays.asList(id);
        return privileges.removeIf(privilege -> revokeIds.contains(privilege.getId()));
    }

    /**
     * Returns {@link PrivilegeEntity} of this group.
     * <p>
     * This method returns a {@link Set} containing {@link PrivilegeEntity}.
     * These define what privileges this group has.
     * <p>
     * Note that this method returns a {@link Unmodifiable} set as the list
     * should not be edited here.
     * To grant or revoke privileges the methods {@link #grantPrivilege(PrivilegeEntity...)} or {@link #revokePrivilege(Long...)} can be used.
     *
     * @return an unmodifiable list containing all privileges this group has.
     * @see #grantPrivilege(PrivilegeEntity...)
     * @see #revokePrivilege(Long...)
     */
    public @NotNull @Unmodifiable Set<PrivilegeEntity> getPrivileges() {
        return Collections.unmodifiableSet(privileges);
    }

    @Override public String toString()
    {
        return "GroupEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", users=" + users +
                ", privileges=" + privileges +
                '}';
    }

    @Override public boolean equals(Object object)
    { // Automatically generated by IntelliJ
        if (this == object) {return true;}
        if (object == null || getClass() != object.getClass()) {return false;}
        GroupEntity that = (GroupEntity) object;
        return Objects.equals(getId(), that.getId());
    }

    @Override public int hashCode()
    { // Automatically generated by IntelliJ
        return Objects.hash(getId());
    }
}
