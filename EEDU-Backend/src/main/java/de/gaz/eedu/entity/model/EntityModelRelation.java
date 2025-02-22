package de.gaz.eedu.entity.model;

import jakarta.validation.constraints.NotNull;

public interface EntityModelRelation<P, M extends Model<P>> extends EntityObject<P>
{
    @NotNull M toModel();
}
