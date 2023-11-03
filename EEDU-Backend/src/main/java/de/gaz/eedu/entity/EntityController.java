package de.gaz.eedu.entity;

import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.entity.model.Model;
import de.gaz.eedu.exception.CreationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@AllArgsConstructor public abstract class EntityController<S extends EntityService<?, M, C>, M extends Model, C extends CreationModel<?>>
{

    @Getter(AccessLevel.PROTECTED) private final S entityService;

    public @NotNull ResponseEntity<M> create(@NotNull C model)
    {
        try
        {
            return ResponseEntity.status(HttpStatus.CREATED).body(getEntityService().create(model));
        }
        catch (CreationException creationException)
        {
            return ResponseEntity.status(creationException.getStatus()).body(null);
        }
    }

    public @NotNull Boolean delete(@NotNull Long id)
    {
        return getEntityService().delete(id);
    }

    public @NotNull ResponseEntity<M> getData(@NotNull Long id)
    {
        return getEntityService().loadById(id).map(ResponseEntity::ok).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}
