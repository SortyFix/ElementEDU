package de.gaz.eedu.user.repository;

import de.gaz.eedu.entity.AbstractEntityRepository;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.repository.GroupRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository @Transactional
public class UserEntityRepositoryImpl extends AbstractEntityRepository<Long, UserEntity> implements UserEntityRepository
{
    private static final String QUERY_TEMPLATE;
    @Getter(AccessLevel.PROTECTED) private final GroupRepository groupRepository;

    static {
        QUERY_TEMPLATE = String.join(" ",
                "SELECT u FROM UserEntity u",
                "LEFT JOIN FETCH u.groups g",
                "LEFT JOIN FETCH g.privileges p",
                "LEFT JOIN FETCH u.themeEntity t"
        );
    }

    public UserEntityRepositoryImpl(@NonNull EntityManager entityManager, @NonNull GroupRepository groupRepository)
    {
        super(entityManager);
        this.groupRepository = groupRepository;
    }

    @Override protected @NonNull TypedQuery<UserEntity> findEntityQuery(@NonNull Long id)
    {
        String sqlQuery = String.join(" ", QUERY_TEMPLATE, "WHERE u.id = :userId");
        return getEntityManager().createQuery(sqlQuery, UserEntity.class).setParameter("userId", id);
    }

    @Override protected @NonNull TypedQuery<UserEntity> findAllEntitiesQuery()
    {
        return getEntityManager().createQuery(QUERY_TEMPLATE, UserEntity.class);
    }

    @Override protected @NonNull UserEntity interceptLoading(@NonNull UserEntity entity)
    {
        String groupName = entity.getAccountType().toString();
        Optional<GroupEntity> groupEntity = getGroupRepository().findEntity(groupName);
        groupEntity.ifPresent(entity::setTypeGroup);
        return super.interceptLoading(entity);
    }
}
