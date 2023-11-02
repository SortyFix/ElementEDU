package de.gaz.eedu.entity;

import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.entity.model.EDUEntity;
import de.gaz.eedu.entity.model.Model;
import de.gaz.eedu.exception.CreationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

//Entity, Model, Create Model
public interface EDUEntityService<E extends EDUEntity, M extends Model, C extends CreationModel<E>>
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
    @NotNull @Transactional(readOnly = true) Optional<E> loadEntityByID(long id);

    /**
     * Loads an {@link E} by a string.
     * <p>
     * This method loads an entity by a specific string.
     * This string must be decided for each service and could variate therefore.
     *
     * @param name of the entity to load.
     * @return an optional, which is empty if no entity was found.
     */
    @NotNull @Transactional(readOnly = true) Optional<E> loadEntityByName(@NotNull String name);

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
    @Transactional(readOnly = true) @Unmodifiable @NotNull List<E> findAllEntities();

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

    @Transactional @NotNull E saveEntity(@NotNull E entity);

    @Transactional(readOnly = true) @NotNull Function<M, E> toEntity();

    @NotNull Function<E, M> toModel();

    @Transactional(readOnly = true) default @NotNull Optional<M> loadById(long id)
    {
        return loadEntityByID(id).map(toModel());
    }

    @Transactional(readOnly = true) default @NotNull Optional<M> loadByName(@NotNull String name)
    {
        return loadEntityByName(name).map(toModel());
    }

    @Transactional(readOnly = true) default @Unmodifiable @NotNull Set<M> findAll()
    {
        return findAllEntities().stream().map(toModel()).collect(Collectors.toSet());
    }

    @Transactional default @NotNull M create(@NotNull C model)
    {
        return toModel().apply(createEntity(model));
    }

    @Transactional @NotNull default M save(@NotNull E entity)
    {
        return toModel().apply(saveEntity(entity));
    }
}
