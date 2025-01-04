package de.gaz.eedu.entity.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.server.ResponseStatusException;

public interface CreationModel<E extends EntityObject> extends Model
{

    @NotNull E toEntity(@NotNull E entity);

    default @NotNull E toEntity(@NotNull E entity, @NotNull CreationFactory<E> factory) throws ResponseStatusException
    {
        return factory.transform(toEntity(entity));
    }
}
