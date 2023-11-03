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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@AllArgsConstructor public abstract class EntityController<S extends EntityService<?, M, C>, M extends Model, C extends CreationModel<?>>
{

    @Getter(AccessLevel.PROTECTED) private final S entityService;

    @PostMapping("/create") public @NotNull ResponseEntity<M> create(@NotNull C model)
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

    @DeleteMapping("/delete/{id}") public @NotNull Boolean delete(@NotNull @PathVariable Long id)
    {
        return getEntityService().delete(id);
    }

    @GetMapping("/get/{id}") public @NotNull ResponseEntity<M> getData(@NotNull @PathVariable Long id)
    {
        return getEntityService().loadById(id).map(ResponseEntity::ok).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}
