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

public interface EntityService<E, M>
{

    @Transactional(Transactional.TxType.SUPPORTS) @NotNull Optional<E> loadEntityByID(long id);

    @Transactional(Transactional.TxType.SUPPORTS) @NotNull Optional<E> loadEntityByName(@NotNull String name);

    @Transactional(Transactional.TxType.SUPPORTS) @Unmodifiable @NotNull List<E> findAllEntities();

    @Transactional(Transactional.TxType.REQUIRED) @NotNull E createEntity(@NotNull M model) throws CreationException;

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

    @Transactional(Transactional.TxType.REQUIRED) default @NotNull M create(@NotNull M model)
    {
        return toModel().apply(createEntity(model));
    }

    @NotNull @Transactional(Transactional.TxType.REQUIRED) default M save(@NotNull E entiy)
    {
        return toModel().apply(saveEntity(entiy));
    }
}
