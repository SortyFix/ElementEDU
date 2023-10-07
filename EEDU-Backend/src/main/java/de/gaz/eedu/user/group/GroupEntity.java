package de.gaz.eedu.user.group;


import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ManyToMany
    private Set<UserEntity> userEntities;
    @SuppressWarnings("JpaDataSourceORMInspection")
    @ManyToMany
    @JoinTable(name = "group_privileges", joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    private Set<PrivilegeEntity> privileges;

    /**
     * Grants {@link PrivilegeEntity}s to this group.
     * <p>
     * This method can add multiple privileges to this group and therefore to all users being part of this group.
     * This uses an {@link Set#addAll(Collection)} and ignores privileges already added.
     * <p>
     * Note that this method accesses the {@code privilegeEntities} set directly as the getter {@link #getPrivilegeEntities()}
     * returns a {@link Unmodifiable} set.
     *
     * @param privilegeEntity which should be added to this method.
     * @return whether a {@link PrivilegeEntity} has been added to this method.
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
     * Note that this method accesses the {@code privilegeEntities} set directly as the getter {@link #getPrivilegeEntities()} returns a {@link Unmodifiable} set.
     *
     * @param id of the {@link PrivilegeEntity} to remove from this group.
     * @return whether a privilege has been revoked from this group.
     */
    public boolean revokePrivilege(@NotNull Long... id) {
        Collection<Long> revokeIds = Arrays.asList(id);
        return privileges.removeIf(privilege -> revokeIds.contains(privilege.getId()));
    }

    public @NotNull @Unmodifiable Set<PrivilegeEntity> getPrivilegeEntities() {
        return Collections.unmodifiableSet(privileges);
    }
}
