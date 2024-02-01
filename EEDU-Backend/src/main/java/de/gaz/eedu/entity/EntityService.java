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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
     * <p>
     * The {@link Transactional} marks that this method performs a database query. The {@link Transactional#readOnly()}
     * makes sure this method only reads from the database but doesn't perform any write operations.
     * </p>
     *
     * @param id of the entity to load.
     * @return an optional containing the {@link E} object or an empty one.
     * @see #loadById(long)
     * @see Transactional
     */
    @Transactional(readOnly = true)
    @NotNull Optional<E> loadEntityByID(long id);

    /**
     * Loads an {@link E} by a string.
     * <p>
     * This method loads an entity by a specific string.
     * This string must be decided for each service and could variate therefore.
     *
     * <p>
     * The {@link Transactional} marks that this method performs a database query. The {@link Transactional#readOnly()}
     * makes sure this method only reads from the database but doesn't perform any write operations.
     * </p>
     *
     * @param name of the entity to load.
     * @return an optional, which is empty if no entity was found.
     * @see Transactional
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
     * <p>
     * The {@link Transactional} marks that this method performs a database query. The {@link Transactional#readOnly()}
     * makes sure this method only reads from the database but doesn't perform any write operations.
     * </p>
     *
     * @return an unmodifiable list containing all entities from the repository defined within the implementation.
     * @see #findAll()
     * @see Transactional
     */
    @Transactional(readOnly = true)
    @NotNull @Unmodifiable List<E> findAllEntities();

    /**
     * Creates an {@link E} based on the data from {@link C}.
     * <p>
     * This method creates a new {@link E} based on the provided {@link C}.
     * {@link C} represents a {@link CreationModel} and therefore has the necessary method for creating
     * a {@link E}.
     *
     * <p>
     * The {@link Transactional} marks that this method performs a database query.
     * If some exception occurs while altering the database within this method, the changes will automatically be roll backed.
     * </p>
     *
     * @param model the model which is used to create a {@link E}
     * @return the created {@link E} which also was saved in the repository.
     * @throws CreationException is thrown when anything went wrong while creating
     * @see Transactional
     */
    @Transactional @NotNull E createEntity(@NotNull C model) throws CreationException;

    /**
     * Deletes an {@link E} from the database.
     * <p>
     * This method deletes an {@link E} by its {@code id} from the database.
     * Note that this can not be reversed after it has been executed.
     * <p>
     * Example Usage:
     * <pre>
     * {@code
     * EntityService service = new SomeServiceImplementation();
     * ExampleE entity = service.saveEntity(new ExampleE());
     *
     * System.out.println(entity.getId()); // Output: 1
     * service.delete(1); // returns true, because it exists
     * service.delete(15) // returns false, because it does not exists.
     *
     * }
     * </pre>
     *
     * <p>
     * The {@link Transactional} marks that this method performs a database query.
     * If some exception occurs while altering the database within this method, the changes will automatically be roll backed.
     * </p>
     *
     * @param id the id of the {@link E} to delete.
     * @return whether an {@link E} has been deleted.
     * @see Transactional
     */
    @Transactional boolean delete(long id);

    /**
     * Saves multiply {@link E}s in the database.
     * <p>
     * Unlike {@link #saveEntity(EntityObject)} this method saves or creates multiple entries in the database.
     * This should be used when mass updating entities, like for example making every user part of a specific group.
     *
     * <p>
     * Example Usage:
     * <pre>
     * {@code
     * EntityService service = new SomeServiceImplementation();
     *
     * List<E> listOfE = new ArrayList<>();
     *
     * E first = new SomeE();
     * E second = new SomeClassImplementingE();
     *
     * listOfE.add(first);
     * listOfE.add(second);
     *
     * service.saveEntity(listOfE);
     * System.out.println(service.loadEntityByIDSafe(1)); // Output: first E
     * System.out.println(service.loadEntityByIDSafe(2)); // Output: second E
     *
     * first.setDummy("new");
     *
     * System.out.println(service.loadEntityByIDSafe(1)); // Output: first E with the updated value
     * System.out.println(service.loadEntityByIDSafe(2)); // Output: second E
     * }
     * </pre>
     * This update is ID based, which means it will always override the entity in the database with the same id, or
     * create a new if the id is not yet set.
     * Note that every change to the local object must be saved after altering them.
     *
     * <p>
     * The {@link Transactional} marks that this method performs a database query.
     * If some exception occurs while altering the database within this method, the changes will automatically be roll backed.
     * </p>
     *
     * @param entity the entities you want to safe
     * @return a list of the entities that are now saved in the database.
     * @see #saveEntity(EntityObject)
     * @see Transactional
     */
    @Transactional
    @NotNull <T extends E> List<T> saveEntity(@NotNull Iterable<T> entity);

    @Contract(pure = true, value = "-> new")
    @Transactional(readOnly = true) @NotNull Function<M, E> toEntity();

    @Contract(pure = true, value = "-> new")
    @NotNull Function<E, M> toModel();

    /**
     * Saves an {@link E} in the database.
     * <p>
     * This method saves an {@link E} in the database.
     * It either creates a new entry if needed, or override exiting ones when something within this object has changed.
     * <p>
     * Example Usage:
     * <pre>
     * {@code
     * EntityService service = new SomeServiceImplementation();
     *
     * ExampleE entity = new SomeE();
     * service.saveEntity(entity); // creates a new entity in the database
     *
     * entity.setDummy("new")
     * service.saveEntity(entity); // updates the exiting entity with the new values
     * }
     * </pre>
     * The update is id based, which means it will always override the entity in the database with the same id, or create
     * a new if the id is not yet set.
     * Note that every change to the local object must be saved after altering them.
     * <p>
     * The {@link Transactional} marks that this method performs a database query.
     * If some exception occurs while altering the database within this method, the changes will automatically be roll backed.
     * </p>
     *
     * @param entity the entity to add or update.
     * @return the saved {@link E} which is now saved in the database.
     * @see #saveEntity(Iterable)
     * @see Transactional
     */
    @Transactional default <T extends E> @NotNull T saveEntity(@NotNull T entity)
    {
        return saveEntity(Collections.singleton(entity)).get(0);
    }

    /**
     * This method loads an {@link E}
     *
     * @param id
     * @return
     * @throws EntityUnknownException
     */
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
