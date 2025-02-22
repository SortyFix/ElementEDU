package de.gaz.eedu.user.group.repository;

import de.gaz.eedu.entity.AbstractEntityRepository;
import de.gaz.eedu.user.AccountType;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.privileges.PrivilegeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository
public class GroupEntityRepositoryImpl extends AbstractEntityRepository<Long, GroupEntity> implements GroupEntityRepository
{
    @Getter(AccessLevel.PROTECTED)
    private final PrivilegeRepository privilegeRepository;

    public GroupEntityRepositoryImpl(@NonNull EntityManager entityManager, @NotNull PrivilegeRepository privilegeRepository)
    {
        super(entityManager);
        this.privilegeRepository = privilegeRepository;
    }

    @Override protected @NonNull TypedQuery<GroupEntity> findEntityQuery(@NonNull Long id)
    {
        String sql = "SELECT g FROM GroupEntity g LEFT JOIN FETCH g.privileges p WHERE g.id = :groupId";
        return getEntityManager().createQuery(sql, GroupEntity.class).setParameter("groupId", id);
    }

    @Override protected @NonNull TypedQuery<GroupEntity> findAllEntitiesQuery()
    {
        String sql = "SELECT g FROM GroupEntity g LEFT JOIN FETCH g.privileges p";
        return getEntityManager().createQuery(sql, GroupEntity.class);
    }

    @Override public @NotNull Optional<GroupEntity> findEntityByName(@NotNull String name)
    {
        String sql = "SELECT g FROM GroupEntity g LEFT JOIN FETCH g.privileges p WHERE g.name = :name";
        TypedQuery<GroupEntity> query = getEntityManager().createQuery(sql, GroupEntity.class).setParameter("name", name);
        return query.getResultList().stream().findFirst().map(this::interceptLoading);
    }

    @Override protected @NonNull GroupEntity interceptLoading(@NonNull GroupEntity entity)
    {
        if(!Objects.equals(entity.getName(), AccountType.ADMINISTRATOR.toString()))
        {
            return super.interceptLoading(entity);
        }

        // attach all privileges to administrator before loading
        entity.setSpoofedPrivileges(getPrivilegeRepository().findAll().stream().toList());
        return super.interceptLoading(entity);
    }
}
