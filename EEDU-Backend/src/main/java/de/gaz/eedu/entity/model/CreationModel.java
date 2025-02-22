package de.gaz.eedu.entity.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.server.ResponseStatusException;

public interface CreationModel<P, E extends EntityObject<P>> extends Model<P>
{

    @NotNull E toEntity(@NotNull E entity);

    default @NotNull E toEntity(@NotNull E entity, @NotNull CreationFactory<P, E> factory) throws ResponseStatusException
    {
        return factory.apply(toEntity(entity));
    }
}
