package de.gaz.eedu.entity;

import de.gaz.eedu.entity.model.EntityObject;
import lombok.NonNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EntityRepository<T extends EntityObject>
{
    @NonNull Optional<T> findEntity(@NonNull Long id);

    @NonNull @Unmodifiable Set<T> findAllEntities();

    @NonNull @Unmodifiable List<T> saveAllEntities(@NonNull Iterable<T> entities);
}
