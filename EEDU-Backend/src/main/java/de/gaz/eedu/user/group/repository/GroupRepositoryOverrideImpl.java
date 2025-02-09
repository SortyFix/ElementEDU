package de.gaz.eedu.user.group.repository;

import de.gaz.eedu.user.AccountType;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryOverrideImpl implements GroupRepositoryOverride
{
    @PersistenceContext private final EntityManager entityManager;

    @Override public @NotNull Optional<GroupEntity> findByIdEagerly(@NotNull Long id)
    {
        String sql = "SELECT g FROM GroupEntity g LEFT JOIN FETCH g.privileges p WHERE g.id = :groupId";
        TypedQuery<GroupEntity> query = entityManager.createQuery(sql, GroupEntity.class);
        query.setParameter("groupId", id);

        return query.getResultList().stream().findFirst().map(this::attachPrivileges);
    }

    @Override public @NotNull  @Unmodifiable Set<GroupEntity> findAllEagerly()
    {
        String sql = "SELECT g FROM GroupEntity g LEFT JOIN FETCH g.privileges p";
        TypedQuery<GroupEntity> query = entityManager.createQuery(sql, GroupEntity.class);
        return query.getResultStream().map(this::attachPrivileges).collect(Collectors.toUnmodifiableSet());
    }

    private @NotNull GroupEntity attachPrivileges(@NotNull GroupEntity groupEntity)
    {
        if(!Objects.equals(groupEntity.getName(), AccountType.ADMINISTRATOR.toString()))
        {
            return groupEntity;
        }

        TypedQuery<PrivilegeEntity> query = entityManager.createQuery("SELECT p FROM PrivilegeEntity p", PrivilegeEntity.class);
        groupEntity.grantPrivilege(query.getResultList().toArray(PrivilegeEntity[]::new));
        return groupEntity;
    }
}
