package de.gaz.eedu.entity.model;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CreationFactory<E extends EDUEntity>
{

    @NotNull E transform(@NotNull E entity);

}
