package de.gaz.eedu.entity.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.server.ResponseStatusException;

@FunctionalInterface
public interface CreationFactory<E extends EntityObject>
{

    @NotNull E transform(@NotNull E entity) throws ResponseStatusException;

}
