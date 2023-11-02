package de.gaz.eedu.entity.model;

import org.jetbrains.annotations.NotNull;

public interface CreationModel<E extends EDUEntity> extends Model
{

    @NotNull String name();

    @NotNull E toEntity();

    default E toEntity(@NotNull CreationFactory<E> factory)
    {
        return factory.transform(toEntity());
    }
}
