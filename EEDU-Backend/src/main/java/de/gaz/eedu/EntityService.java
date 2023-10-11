package de.gaz.eedu;

import de.gaz.eedu.exception.CreationException;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface EntityService<E, M, C>
{

    /**
     * Loads an entity {@link E} by its id.
     * <p>
     * This method is an abstract method loading an entity portrayed by the generic type {@link E}.
     * <p>
     * Note that this entity can be ported into a {@link M} model using {@link #loadById(long)}.
     * This {@link M} most likely represents a model of the actual entity which is json friendly.
     *
     * @param id of the entity to load.
     * @return an optional containing the {@link E} object or an empty one.
     * @see #loadById(long)
     */
    @Transactional(Transactional.TxType.SUPPORTS) @NotNull Optional<E> loadEntityByID(long id);
    
    /**
     * Loads an {@link E} by a string.
     * <p>
     * This method loads an entity by a specific string.
     * This string must be decided for each service and could variate therefore.
     * <p>
     * Note that this method has a Support {@link Transactional} which tells jarkata that this should be called in a
     * support context.
     *
     * @param name of the entity to load.
     * @return an optional, which is empty if no entity was found.
     */
    @Transactional(Transactional.TxType.SUPPORTS) @NotNull Optional<E> loadEntityByName(@NotNull String name);

    @Transactional(Transactional.TxType.SUPPORTS) @Unmodifiable @NotNull List<E> findAllEntities();

    @Transactional(Transactional.TxType.REQUIRED) @NotNull E createEntity(@NotNull C model) throws CreationException;

    @Transactional(Transactional.TxType.REQUIRED) boolean delete(long id);

    @NotNull E saveEntity(@NotNull E entity);

    @NotNull Function<M, E> toEntity();

    @NotNull Function<E, M> toModel();

    @Transactional(Transactional.TxType.SUPPORTS) default @NotNull Optional<M> loadById(long id)
    {
        return loadEntityByID(id).map(toModel());
    }

    @Transactional(Transactional.TxType.SUPPORTS) default @NotNull Optional<M> loadByName(@NotNull String name)
    {
        return loadEntityByName(name).map(toModel());
    }

    @Transactional(Transactional.TxType.SUPPORTS) default @Unmodifiable @NotNull Set<M> findAll()
    {
        return findAllEntities().stream().map(toModel()).collect(Collectors.toSet());
    }

    @Transactional(Transactional.TxType.REQUIRED) default @NotNull M create(@NotNull C model)
    {
        return toModel().apply(createEntity(model));
    }

    @NotNull @Transactional(Transactional.TxType.REQUIRED) default M save(@NotNull E entity)
    {
        return toModel().apply(saveEntity(entity));
    }
}
