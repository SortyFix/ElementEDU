package de.gaz.eedu.entity.model;

import org.jetbrains.annotations.NotNull;

public interface EntityModel<T> extends Model<T>
{
    @NotNull T id();
}
