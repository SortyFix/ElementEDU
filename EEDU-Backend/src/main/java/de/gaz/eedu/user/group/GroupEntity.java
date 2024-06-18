package de.gaz.eedu.user.group;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.group.model.GroupModel;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class represents a database entry of a group.
 * <p>
 * This is the representation of a database entry of a group. It contains its id, name, users and the
 * privileges this group has.
 * Groups are used to cluster {@link UserEntity} together and manage their access precisely. This is archived by
 * adding {@link PrivilegeEntity}s to
 * this object and then assign users to it.
 * <p>
 * Note that a {@link UserEntity} can be part of multiple groups.
 *
 * @author ivo
 * @see UserEntity
 * @see PrivilegeEntity
 */
@Entity @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Table(name = "group_entity")
public class GroupEntity implements EntityModelRelation<GroupModel>
{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(AccessLevel.NONE) private Long id;
    private String name;

    @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY) @JsonBackReference @Setter(AccessLevel.PRIVATE)
    private Set<UserEntity> users = new HashSet<>();

    @ManyToMany @JsonManagedReference
    @JoinTable(name = "group_privileges", joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    private Set<PrivilegeEntity> privileges;

    /**
     * Creates an instance with a {@link Set} of users.
     * <p>
     * This constructor creates an instance of the group entity only requiring a users set. This is necessary
     * as this reference is not managed in this class but outside of it.
     * <p>
     * As a result, this constructor prioritizes the users set, primarily because the necessary data might not
     * be immediately available when first creating the group entity. Other properties of the group entity,
     * like privileges or name, can be changed later as required using their respective setter methods.
     * <p>
     * Therefore, this constructor provides greater flexibility when initial data is minimal or when the
     * group entity undergoes significant changes post-instantiation.
     *
     * @param users the set of {@link UserEntity} objects to be associated with this group.
     */
    public GroupEntity(@NotNull Set<UserEntity> users)
    {
        this.users = users;
    }

    public @NotNull @Unmodifiable Set<GrantedAuthority> toSpringSecurity()
    {
        return Stream.concat(getAuthorities().stream(), Stream.of(toRole())).collect(Collectors.toSet());
    }


    @Override public @NotNull GroupModel toModel()
    {
        return new GroupModel(getId(), getName());
    }

    /**
     * This method is used to convert the name of a user/role to a format that aligns with spring API,
     * specifically {@link org.springframework.security.core.GrantedAuthority}.
     * <p>
     * The format adopted is to prefix the name with "ROLE_", this is because the spring API
     * uses roles to check authorizations and expects role names to be in specific formats.
     * GrantedAuthority is a key interface in the Spring Security framework, representing an
     * authority that's been granted to an Authentication object. The main benefit of this
     * convention is that it enables method level security.
     *
     * @return a new {@link org.springframework.security.core.authority.SimpleGrantedAuthority} object which
     * incorporates the adjusted (prefixed) name/role. SimpleGrantedAuthority is a basic,
     * immutable implementation of {@link org.springframework.security.core.GrantedAuthority}.
     * @see org.springframework.security.core.authority.SimpleGrantedAuthority
     * @see org.springframework.security.core.GrantedAuthority
     */
    private @NotNull GrantedAuthority toRole()
    {
        return new SimpleGrantedAuthority("ROLE_" + getName().toUpperCase());
    }

    /**
     * This method returns a set of authorities/permissions that a principal has.
     * <p>
     * It makes use of Java Streams to transform the set of privilege entities
     * into a set of GrantedAuthority. This is done via a map operation that
     * uses a method reference PrivilegeEntity::toAuthority to do the transformation.
     * Finally, it uses the collect() method from the Streams API to convert
     * the Stream into a Set, thus giving us a Set of GrantedAuthorities.
     * Importantly, this method is annotated with '@Unmodifiable', which means
     * that the returned Set instance is unmodifiable and any attempt to modify it would
     * result in an UnsupportedOperationException.
     *
     * @return an unmodifiable set of {@link org.springframework.security.core.GrantedAuthority} objects, each
     * representing a user authority (privilege). It may be empty but is never null.
     * @see org.springframework.security.core.GrantedAuthority
     */
    private @NotNull @Unmodifiable Set<GrantedAuthority> getAuthorities()
    {
        return getPrivileges().stream().map(PrivilegeEntity::toAuthority).collect(Collectors.toSet());
    }

    /**
     * Grants various privileges to this group and saves the state using the provided group service.
     * The privileges are specified via their entities.
     * The method will update the state of the group and then use the service to save the updated entity state.
     * <p>
     * The process is carried out within a transaction to ensure that all changes are either applied in full or not
     * at all.
     * This is done to maintain data integrity.
     *
     * @param groupService    the service used for saving the group entity after granting the privileges.
     * @param privilegeEntity the privileges to be granted.
     * @return true if a privilege was successfully granted and the group entity was saved, false otherwise.
     */
    @Transactional
    public boolean grantPrivilege(@NotNull GroupService groupService, @NotNull PrivilegeEntity... privilegeEntity)
    {
        if (grantPrivilege(privilegeEntity))
        {
            groupService.saveEntity(this);
            return true;
        }
        return false;
    }

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
    public boolean grantPrivilege(@NotNull PrivilegeEntity... privilegeEntity)
    {
        return privileges.addAll(Arrays.stream(privilegeEntity).collect(Collectors.toSet()));
    }

    /**
     * Revokes various privileges from this group and saves the state using the provided group service.
     * The privileges are specified via their IDs. The method will invoke {@link #revokePrivilege(Long...)}
     * to perform the actual removal of the privileges, and then use the service to save the updated entity state.
     * <p>
     * The process is carried out within a transaction, ensuring that all changes are either applied in full
     * or not at all, in order to maintain data integrity.
     *
     * @param groupService the service used for saving the group entity after revoking the privileges
     * @param id           the IDs of the privileges to revoke
     * @return true if a privilege was successfully revoked and the group entity was saved, false otherwise
     */
    @Transactional public boolean revokePrivilege(@NotNull GroupService groupService, @NotNull Long... id)
    {
        if (revokePrivilege(id))
        {
            groupService.saveEntity(this);
            return true;
        }
        return false;
    }

    /**
     * Revokes {@link PrivilegeEntity}s from this group.
     * <p>
     * This method removes privileges from this group and therefore from all users having this group.
     * This uses the {@link Set#removeIf(Predicate)} method which gets rid of duplicated entries if there was some
     * kind of error.
     * <p>
     * Note that this method accesses the {@code privilegeEntities} set directly as the getter
     * {@link #getPrivileges()} returns a {@link Unmodifiable} set.
     *
     * @param id of the {@link PrivilegeEntity} to remove from this group.
     * @return whether a privilege has been revoked from this group.
     * @see #grantPrivilege(PrivilegeEntity...)
     * @see #getPrivileges()
     */
    public boolean revokePrivilege(@NotNull Long... id)
    {
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
     * To grant or revoke privileges the methods {@link #grantPrivilege(PrivilegeEntity...)} or
     * {@link #revokePrivilege(Long...)} can be used.
     *
     * @return an unmodifiable list containing all privileges this group has.
     * @see #grantPrivilege(PrivilegeEntity...)
     * @see #revokePrivilege(Long...)
     */
    public @NotNull @Unmodifiable Set<PrivilegeEntity> getPrivileges()
    {
        return Collections.unmodifiableSet(privileges);
    }

    @Override public boolean deleteManagedRelations()
    {
        if(this.privileges.isEmpty())
        {
            return false;
        }
        this.privileges.clear();
        return true;
    }

    @Override public String toString()
    {
        return "GroupEntity{" + "id=" + id + ", name='" + name + '\'' + ", users=" + users + ", privileges=" + privileges + '}';
    }

    @Override public boolean equals(Object object)
    { // Automatically generated by IntelliJ
        if (this == object) { return true; }
        if (object == null || getClass() != object.getClass()) { return false; }
        GroupEntity that = (GroupEntity) object;
        return Objects.equals(getId(), that.getId());
    }

    @Override public int hashCode()
    { // Automatically generated by IntelliJ
        return Objects.hash(getId());
    }
}
