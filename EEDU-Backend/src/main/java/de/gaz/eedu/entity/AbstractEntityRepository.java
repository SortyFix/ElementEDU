package de.gaz.eedu.entity;

import de.gaz.eedu.entity.model.EntityObject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Getter(AccessLevel.PROTECTED)
public abstract class AbstractEntityRepository<T extends EntityObject> implements EntityRepository<T>
{
    @PersistenceContext
    private final EntityManager entityManager;

    public AbstractEntityRepository(@NonNull EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    protected abstract @NonNull TypedQuery<T> findEntityQuery(@NonNull Long id);

    protected abstract @NonNull TypedQuery<T> findAllEntitiesQuery();

    @Override public @NonNull Optional<T> findEntity(@NonNull Long id)
    {
        return findEntityQuery(id).getResultList().stream().findFirst().map(this::interceptLoading);
    }

    @Override public @NonNull @Unmodifiable Set<T> findAllEntities()
    {
        return findAllEntitiesQuery().getResultList().stream().map(this::interceptLoading).collect(Collectors.toUnmodifiableSet());
    }

    @Override public @NonNull @Unmodifiable List<T> saveAllEntities(@NonNull Iterable<T> entities)
    {
        List<T> preservedEntities = new ArrayList<>();
        for (T entity : entities) { preservedEntities.add(getEntityManager().merge(this.interceptSaving(entity))); }

        getEntityManager().flush();
        return preservedEntities;
    }

    protected @NonNull T interceptLoading(@NonNull T entity) { return entity; }

    protected @NonNull T interceptSaving(@NonNull T entity) { return entity; }
}
