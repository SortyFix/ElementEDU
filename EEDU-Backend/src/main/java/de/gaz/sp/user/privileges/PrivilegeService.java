package de.gaz.sp.user.privileges;

import de.gaz.sp.user.exception.NameOccupiedException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class PrivilegeService {

    @Getter(AccessLevel.PROTECTED) private final PrivilegeRepository privilegeRepository;

    public @NotNull Optional<PrivilegeEntity> loadPrivilegeEntityById(@NotNull Long id)
    {
        return getPrivilegeRepository().findById(id);
    }

    public Optional<PrivilegeModel> loadPrivilegeById(@NotNull Long id)
    {
        return loadPrivilegeEntityById(id).map(toModel());
    }

    public @NotNull Optional<PrivilegeEntity> loadPrivilegeEntityByName(@NotNull String name)
    {
        return getPrivilegeRepository().findByName(name);
    }

    public @NotNull Optional<PrivilegeModel> loadPrivilegeByName(@NotNull String name)
    {
        return loadPrivilegeEntityByName(name).map(toModel());
    }

    public @NotNull PrivilegeModel createPrivilege(@NotNull PrivilegeModel privilegeModel)
    {
        getPrivilegeRepository().findByName(privilegeModel.name()).map(toModel()).ifPresent(model ->
        {
            throw new NameOccupiedException(model.name());
        });

        return toModel().apply(getPrivilegeRepository().save(toGroup().apply(privilegeModel)));
    }

    @Contract(pure = true)
    private @NotNull Function<PrivilegeModel, PrivilegeEntity> toGroup()
    {
        return groupModel -> new PrivilegeEntity(groupModel.id(), groupModel.name(), groupModel.groupEntities());
    }

    @Contract(pure = true)
    private @NotNull Function<PrivilegeEntity, PrivilegeModel> toModel()
    {
        return privilegeEntity -> new PrivilegeModel(privilegeEntity.getId(), privilegeEntity.getName(), privilegeEntity.getGroupEntities());
    }

}
