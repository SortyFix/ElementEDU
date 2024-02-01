package de.gaz.eedu.user.privileges;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.GroupRepository;
import de.gaz.eedu.user.privileges.model.PrivilegeCreateModel;
import de.gaz.eedu.user.privileges.model.PrivilegeModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Service @AllArgsConstructor @Getter(AccessLevel.PROTECTED) public class PrivilegeService implements EntityService<PrivilegeEntity,
        PrivilegeModel,
        PrivilegeCreateModel>
{

    private final PrivilegeRepository privilegeRepository;
    private final GroupRepository groupRepository;

    @Override public @NotNull Optional<PrivilegeEntity> loadEntityByID(long id)
    {
        return getPrivilegeRepository().findById(id);
    }

    @Override public @NotNull Optional<PrivilegeEntity> loadEntityByName(@NotNull String name)
    {
        return getPrivilegeRepository().findByName(name);
    }

    @Override public @Unmodifiable @NotNull List<PrivilegeEntity> findAllEntities()
    {
        return getPrivilegeRepository().findAll();
    }

    @Override public @NotNull PrivilegeEntity createEntity(@NotNull PrivilegeCreateModel privilegeCreateModel) throws CreationException
    {
        getPrivilegeRepository().findByName(privilegeCreateModel.name()).ifPresent(occupiedName ->
        {
            throw new NameOccupiedException(occupiedName.getName());
        });

        return getPrivilegeRepository().save(privilegeCreateModel.toEntity(new PrivilegeEntity()));
    }

    @Override public boolean delete(long id)
    {
        return getPrivilegeRepository().findById(id).map(privilegeEntity ->
        {
            // Delete this privilege from the groups
            Set<GroupEntity> groups = privilegeEntity.getGroupEntities();
            for(GroupEntity entity : groups)
            {
                entity.revokePrivilege(privilegeEntity.getId());
            }
            getGroupRepository().saveAll(groups);

            getPrivilegeRepository().deleteById(id);
            return true;
        }).orElse(false);
    }

    @Override
    public <T extends PrivilegeEntity> @NotNull List<T> saveEntity(@NotNull Iterable<T> entity)
    {
        return getPrivilegeRepository().saveAll(entity);
    }

    @Transactional @Override public @NotNull Function<PrivilegeModel, PrivilegeEntity> toEntity()
    {
        return privilegeModel -> loadEntityByID(privilegeModel.id()).orElseThrow(() -> new EntityUnknownException(
                privilegeModel.id()));
    }

    @Override @Contract(pure = true) public @NotNull Function<PrivilegeEntity, PrivilegeModel> toModel()
    {
        return PrivilegeEntity::toModel;
    }
}
