package de.gaz.eedu.user.group;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.group.model.GroupModel;
import de.gaz.eedu.user.model.SimpleUserModel;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import de.gaz.eedu.user.privileges.model.SimplePrivilegeModel;
import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
@Entity @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Table(name = "group_entity") public class GroupEntity implements EntityModelRelation<GroupModel>
{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(AccessLevel.NONE) private Long id;
    private String name;
    private boolean twoFactorRequired;
    @ManyToMany(mappedBy = "groups") @JsonBackReference @Setter(AccessLevel.PRIVATE) private Set<UserEntity> users =
		    new HashSet<>();
    @ManyToMany @JsonManagedReference @JoinTable(name = "group_privileges", joinColumns = @JoinColumn(name =
		    "group_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "privilege_id",
		    referencedColumnName = "id")) private Set<PrivilegeEntity> privileges;

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

    @Override public @NotNull GroupModel toModel()
    {
        return new GroupModel(getId(),
                getName(),
                getUsers().stream().map(UserEntity::toSimpleModel).toArray(SimpleUserModel[]::new),
                getPrivileges().stream().map(PrivilegeEntity::toSimpleModel).toArray(SimplePrivilegeModel[]::new));
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
    @Transactional public boolean grantPrivilege(@NotNull GroupService groupService,
		    @NotNull PrivilegeEntity... privilegeEntity)
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
        // Filter already granted privileges out
        Predicate<PrivilegeEntity> privilegeEntityPredicate = requestedPrivilege -> privileges.stream()
                .noneMatch(presentPrivilege -> Objects.equals(presentPrivilege, requestedPrivilege));
        return privileges.addAll(Arrays.stream(privilegeEntity)
                .filter(privilegeEntityPredicate)
                .collect(Collectors.toSet()));
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

    @Override public String toString()
    {
        return "GroupEntity{" + "id=" + id + ", name='" + name + '\'' + ", users=" + users + ", privileges=" + privileges + '}';
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
