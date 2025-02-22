package de.gaz.eedu.entity.model;

import java.util.function.Function;

@FunctionalInterface
public interface CreationFactory<P, E extends EntityObject<P>> extends Function<E, E> {}
