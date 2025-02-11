package de.gaz.eedu.entity;

import de.gaz.eedu.entity.model.EntityObject;
import lombok.NonNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;

public interface OverriddenEagerRepository<T extends EntityObject>
{
    @NonNull Optional<T> findByIdEagerly(@NonNull Long id);

    @NonNull @Unmodifiable Set<T> findAllEagerly();
}
