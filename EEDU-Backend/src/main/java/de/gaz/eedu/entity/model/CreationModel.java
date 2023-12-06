package de.gaz.eedu.entity.model;

import org.jetbrains.annotations.NotNull;

public interface CreationModel<E extends EntityObject> extends Model
{

    @NotNull String name();

    @NotNull E toEntity(@NotNull E entity);

    default @NotNull E toEntity(@NotNull E entity, @NotNull CreationFactory<E> factory)
    {
        return factory.transform(toEntity(entity));
    }
}
