package de.gaz.eedu.entity.model;

import org.jetbrains.annotations.NotNull;

public interface CreationModel<E extends EDUEntity> extends Model
{

    @NotNull E toEntity();

}
