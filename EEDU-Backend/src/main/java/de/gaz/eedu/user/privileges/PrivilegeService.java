package de.gaz.eedu.user.privileges;

import de.gaz.eedu.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.exception.NameOccupiedException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class PrivilegeService implements EntityService<PrivilegeEntity, PrivilegeModel> {

    @Getter(AccessLevel.PROTECTED) private final PrivilegeRepository privilegeRepository;

    @Override
    public @NotNull Optional<PrivilegeEntity> loadEntityByID(long id) {
        return getPrivilegeRepository().findById(id);
    }

    @Override
    public @NotNull Optional<PrivilegeEntity> loadEntityByName(@NotNull String name) {
        return getPrivilegeRepository().findByName(name);
    }

    @Override
    public @Unmodifiable @NotNull List<PrivilegeEntity> findAllEntities() {
        return getPrivilegeRepository().findAll();
    }

    @Override
    public @NotNull PrivilegeEntity createEntity(@NotNull PrivilegeModel model) throws CreationException {
        getPrivilegeRepository().findByName(model.name()).map(toModel()).ifPresent(occupiedName ->
        {
            throw new NameOccupiedException(occupiedName.name());
        });

        return getPrivilegeRepository().save(toEntity().apply(model));
    }

    @Override
    public @NotNull PrivilegeEntity saveEntity(@NotNull PrivilegeEntity entity) {
        return getPrivilegeRepository().save(entity);
    }

    @Override
    @Contract(pure = true)
    public @NotNull Function<PrivilegeModel, PrivilegeEntity> toEntity()
    {
        return groupModel -> new PrivilegeEntity(groupModel.id(), groupModel.name(), groupModel.groupEntities());
    }

    @Override
    @Contract(pure = true)
    public @NotNull Function<PrivilegeEntity, PrivilegeModel> toModel()
    {
        return privilegeEntity -> new PrivilegeModel(privilegeEntity.getId(), privilegeEntity.getName(), privilegeEntity.getGroupEntities());
    }
}
