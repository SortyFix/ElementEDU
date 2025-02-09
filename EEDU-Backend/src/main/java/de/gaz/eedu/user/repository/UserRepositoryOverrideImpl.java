package de.gaz.eedu.user.repository;

import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.group.GroupEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Transactional
@RequiredArgsConstructor
public class UserRepositoryOverrideImpl implements UserRepositoryOverride
{
    @PersistenceContext private final EntityManager entityManager;
    private static final String QUERY_TEMPLATE;

    static {
        QUERY_TEMPLATE =
                "SELECT u FROM UserEntity u " +
                "LEFT JOIN FETCH u.groups g " +
                "LEFT JOIN FETCH g.privileges p " +
                "LEFT JOIN FETCH u.themeEntity t";
    }

    @Override public @NotNull Optional<UserEntity> findByIdEagerly(@NotNull Long id)
    {
        String sqlQuery = String.join(" ", QUERY_TEMPLATE, "WHERE u.id = :userId");
        TypedQuery<UserEntity> query = entityManager.createQuery(sqlQuery, UserEntity.class);
        query.setParameter("userId", id);

        Optional<UserEntity> userOptional = query.getResultList().stream().findFirst();

        return userOptional.map(this::attachTypeGroup);
    }

    @Override public @Unmodifiable @NotNull Set<UserEntity> findAllEagerly()
    {
        TypedQuery<UserEntity> query = entityManager.createQuery(QUERY_TEMPLATE, UserEntity.class);
        return query.getResultStream().map(this::attachTypeGroup).collect(Collectors.toUnmodifiableSet());
    }

    @Contract("_ -> param1") private @NotNull UserEntity attachTypeGroup(@NotNull UserEntity user)
    {
        TypedQuery<GroupEntity> groupQuery = entityManager.createQuery(
                "SELECT g FROM GroupEntity g WHERE g.name = :groupName",
                GroupEntity.class
        );

        groupQuery.setParameter("groupName", user.getAccountType().toString());

        groupQuery.getResultList().stream().findFirst().ifPresent(user::attachGroups);
        return user;
    }
}
