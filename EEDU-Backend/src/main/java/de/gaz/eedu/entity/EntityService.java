package de.gaz.eedu.entity;

import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.entity.model.EntityObject;
import de.gaz.eedu.entity.model.Model;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

//Entity, Model, Create Model
public interface EntityService<E extends EntityObject, M extends Model, C extends CreationModel<E>>
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
    @Transactional(readOnly = true)
    @NotNull Optional<E> loadEntityByID(long id);

    /**
     * Loads an {@link E} by a string.
     * <p>
     * This method loads an entity by a specific string.
     * This string must be decided for each service and could variate therefore.
     *
     * @param name of the entity to load.
     * @return an optional, which is empty if no entity was found.
     */
    @Transactional(readOnly = true)
    @NotNull Optional<E> loadEntityByName(@NotNull String name);

    /**
     * Loads all entities as {@link E}.
     * <p>
     * This method is responsible for loading all entities from a repository.
     * This repository is defined within the implementation of this class.
     * <p>
     * Note that when the data should be prepared for the frontend, consider using {@link #findAll()} as this
     * automatically casts all {@link E} to {@link M}.
     *
     * @return an unmodifiable list containing all entities from the repository defined within the implementation.
     * @see #findAll()
     */
    @Transactional(readOnly = true)
    @NotNull @Unmodifiable List<E> findAllEntities();

    /**
     * Creates an {@link E} based on the data from {@link C}.
     * <p>
     * This method creates a new {@link E} based on the provided {@link C}.
     * {@link C} represents a {@link CreationModel} and therefore has the necessary method for creating
     * a {@link E}.
     * @param model the model which is used to create a {@link E}
     * @return the created {@link E} which also was saved in the repository.
     * @throws CreationException is thrown when anything went wrong while creating
     */
    @Transactional @NotNull E createEntity(@NotNull C model) throws CreationException;

    @Transactional boolean delete(long id);

    default @Transactional @NotNull E saveEntity(@NotNull E entity)
    {
        return saveEntity(Collections.singleton(entity)).get(0);
    }

    @Transactional @NotNull List<E> saveEntity(@NotNull Iterable<E> entity);

    @Contract(pure = true, value = "-> new")
    @Transactional(readOnly = true) @NotNull Function<M, E> toEntity();

    @Contract(pure = true, value = "-> new")
    @NotNull Function<E, M> toModel();

    @Transactional(readOnly = true)
    default @NotNull E loadEntityByIDSafe(long id) throws EntityUnknownException
    {
        return loadEntityByID(id).orElseThrow(() -> new EntityUnknownException(id));
    }

    @Transactional(readOnly = true) default @NotNull M loadByIdSafe(long id) throws EntityUnknownException
    {
        return toModel().apply(loadEntityByIDSafe(id));
    }

    @Transactional(readOnly = true) default @NotNull Optional<M> loadById(long id)
    {
        return loadEntityByID(id).map(toModel());
    }

    @Transactional(readOnly = true) default @NotNull Optional<M> loadByName(@NotNull String name)
    {
        return loadEntityByName(name).map(toModel());
    }

    @Transactional(readOnly = true)
    default @NotNull @Unmodifiable Set<M> findAll()
    {
        return findAllEntities().stream().map(toModel()).collect(Collectors.toSet());
    }

    @Transactional default @NotNull M create(@NotNull C model)
    {
        return toModel().apply(createEntity(model));
    }

    @Transactional
    default @NotNull M save(@NotNull E entity)
    {
        return toModel().apply(saveEntity(entity));
    }
}
