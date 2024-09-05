package de.gaz.eedu.entity;

import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.entity.model.EntityModel;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

//Entity, Model, Create Model
@Slf4j
public abstract class EntityService<R extends JpaRepository<E, Long>, E extends EntityModelRelation<M>, M extends EntityModel, C extends CreationModel<E>> extends EntityExceptionHandler
{
    @NotNull public abstract R getRepository();

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
    @Transactional(readOnly = true) public @NotNull Optional<E> loadEntityById(long id)
    {
        return getRepository().findById(id);
    }

    /**
     * Loads multiple {@link E} by their ids.
     * <p>
     * This method loads multiple {@link E} from the databased on their id.
     * If one of the entities does not exist, it won't be in the returned {@link Set}.
     * <p>
     * Note that the set will never be {@code null} but will instead be empty if no matching {@link E} was found.
     *
     * <p>
     * The {@link Transactional} marks that this method performs a database query. The {@link Transactional#readOnly()}
     * makes sure this method only reads from the database but doesn't perform any write operations.
     * </p>
     *
     * @param id an array of all ids that should be loaded.
     * @return a {@link Set} containing all {@link E} that were found. Empty when no {@link E} were found. Never {@code null}
     * @see #loadById(Long...) 
     * @see Transactional
     */
    @Transactional(readOnly = true) public @NotNull @Unmodifiable Set<E> loadEntityById(@NotNull Long... id)
    {
        return Set.copyOf(getRepository().findAllById(List.of(id)));
    }

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
    @Transactional(readOnly = true) public @NotNull @Unmodifiable Set<E> findAllEntities()
    {
        return Set.copyOf(getRepository().findAll());
    }

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
    @Transactional public abstract @NotNull E createEntity(@NotNull C model) throws CreationException;

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
    @Transactional public boolean delete(long id)
    {
        return getRepository().findById(id).map(entry ->
        {
            validate(entry.isDeletable(), unauthorizedThrowable());

            String entityName = entry.getClass().getSimpleName();
            log.info("The system has initiated a deletion request for the entity {} {}.", entityName, id);
            if (entry.deleteManagedRelations())
            {
                save(entry);
            }

            deleteRelations(entry);

            getRepository().deleteById(id);
            log.info("The deletion process of the entity {} {} has been successfully executed.", entityName, id);
            return true;
        }).orElse(false);
    }

    public void deleteRelations(@NotNull E entry) {}

    /**
     * Saves multiply {@link E}s in the database.
     * <p>
     * Unlike {@link #saveEntity(EntityModelRelation)} (EntityObject)} this method saves or creates multiple entries in the database.
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
     * @see #saveEntity(EntityModelRelation)
     * @see Transactional
     */
    @Transactional public @NotNull <T extends E> List<T> saveEntity(@NotNull Iterable<T> entity)
    {
        return getRepository().saveAll(entity);
    }

    /**
     * This returns a {@link Function} for turning a {@link M} into an {@link E}.
     * <p>
     * This method creates a {@link Function} turning a {@link M} into an {@link E}.
     * Note that most likely, it will just the id to load the current entity from the database instead of actually
     * translating it. Therefore, it has the {@link Transactional} annotation.
     * <p>
     * Example Usage:
     * <pre>
     * {@code
     * EntityService service = new SomeServiceImplementation();
     * M model = new SomeM();
     *
     * E translated = service.toEntity().apply(model)
     * }
     * </pre>
     * </p>
     *
     * @return a function for transforming a {@link M} into a {@link E}
     * @see Function
     * @see Contract
     * @see Transactional
     */
    @Contract(pure = true, value = "-> new") @Transactional(readOnly = true) public @NotNull Function<M, E> toEntity()
    {
        return model -> getRepository().findById(model.id()).orElseThrow(() -> new EntityUnknownException(model.id()));
    }

    @Contract(pure = true, value = "-> new") public @NotNull Function<E, M> toModel()
    {
        return EntityModelRelation::toModel;
    }

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
    @Transactional public <T extends E> @NotNull T saveEntity(@NotNull T entity)
    {
        return saveEntity(Collections.singleton(entity)).getFirst();
    }

    /**
     * Loads an {@link E} by its id.
     * <p>
     * Unlike the method {@link #loadEntityById(long)} this method does not return an {@link Optional}.
     * If the given id does not exist, this method will instead throw an {@link EntityUnknownException}.
     * <p>
     * Example Usage:
     * <pre>
     * {@code
     * EntityService service = new SomeServiceImplementation();
     * service.saveEntity(new SomeE());
     *
     * System.out.println(service.loadEntityByIDSafe(1)); // Output: entity1
     * System.out.println(service.loadEntityByIDSafe(2)); // throws EntityUnknownException
     * }
     * </pre>
     *
     * @param id the id of the entry to load.
     * @return the entity from the database.
     * @throws EntityUnknownException is thrown when the entity with the provided id is not present in the database.
     * @see #loadByIdSafe(long)
     * @see #loadEntityById(long)
     */
    @Transactional(readOnly = true) public @NotNull E loadEntityByIDSafe(long id) throws EntityUnknownException
    {
        return loadEntityById(id).orElseThrow(() -> new EntityUnknownException(id));
    }

    /**
     * Loads a {@link M} by its id.
     * <p>
     * This method loads {@link M} by its id. It does this by using {@link #loadEntityByIDSafe(long)} and translates the result into a {@link M} using the {@link #toModel()} {@link Function}
     *
     * @param id the id of the entity to load and transform.
     * @return the transformed entity loaded from the database.
     * @throws EntityUnknownException is thrown when the entity with the provided id is not present in the database.
     * @see #loadEntityByIDSafe(long)
     * @see #loadById(long)
     */
    @Transactional(readOnly = true) public @NotNull M loadByIdSafe(long id) throws EntityUnknownException
    {
        return toModel().apply(loadEntityByIDSafe(id));
    }


    /**
     * Loads a {@link M} by its id.
     * <p>
     * This method load a {@link M} by its id. Unlike {@link #loadByIdSafe(long)} this method does not throw an exception when the entity does not exist.
     * Instead, it will return an empty {@link Optional}.
     * <p>
     * If the entity exists it will then be transformed into a {@link M} using the {@link #toModel()} {@link Function}
     * <p>
     * Example Usage:
     * <pre>
     * {@code
     * EntityService service = new SomeServiceImplementation();
     * service.saveEntity(new SomeE());
     *
     * System.out.println(service.loadById(1)); // Output: Optional[entity1]
     * System.out.println(service.loadById(2)); // Output: Optional[]
     * }
     * </pre>
     *
     * @param id of the entity to load.
     * @return an {@link Optional} containing the {@link M} if it exists or a {@link Optional#empty()}.
     */
    @Transactional(readOnly = true) public @NotNull Optional<M> loadById(long id)
    {
        return loadEntityById(id).map(toModel());
    }

    @Transactional(readOnly = true) public @NotNull @Unmodifiable Set<M> loadById(@NotNull Long... id)
    {
        return loadEntityById(id).stream().map(toModel()).collect(Collectors.toUnmodifiableSet());
    }

    @Transactional(readOnly = true) public @NotNull @Unmodifiable Set<M> findAll()
    {
        return findAllEntities().stream().map(toModel()).collect(Collectors.toSet());
    }

    @Transactional public @NotNull M create(@NotNull C model)
    {
        return toModel().apply(createEntity(model));
    }

    @Transactional public @NotNull M save(@NotNull E entity)
    {
        return toModel().apply(saveEntity(entity));
    }
}
