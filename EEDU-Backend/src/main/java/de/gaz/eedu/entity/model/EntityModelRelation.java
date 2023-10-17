package de.gaz.eedu.entity.model;

import jakarta.validation.constraints.NotNull;

public interface EntityModelRelation<M extends Model> extends EDUEntity
{
    @NotNull M toModel();
}
