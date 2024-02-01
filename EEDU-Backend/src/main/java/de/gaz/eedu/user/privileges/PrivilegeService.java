package de.gaz.eedu.user.privileges;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
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
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.Function;

@Service @AllArgsConstructor @Getter(AccessLevel.PROTECTED) public class PrivilegeService implements EntityService<PrivilegeRepository, PrivilegeEntity, PrivilegeModel, PrivilegeCreateModel>
{

    @Getter(AccessLevel.NONE)
    private final PrivilegeRepository privilegeRepository;
    private final GroupRepository groupRepository;

    @Override
    public @NotNull PrivilegeRepository getRepository()
    {
        return privilegeRepository;
    }

    @Override public @NotNull PrivilegeEntity createEntity(@NotNull PrivilegeCreateModel privilegeCreateModel) throws CreationException
    {
        getRepository().findByName(privilegeCreateModel.name()).ifPresent(occupiedName ->
        {
            throw new NameOccupiedException(occupiedName.getName());
        });

        return getRepository().save(privilegeCreateModel.toEntity(new PrivilegeEntity()));
    }

    @Override public boolean delete(long id)
    {
        return getRepository().findById(id).map(privilegeEntity ->
        {
            // Delete this privilege from the groups
            Set<GroupEntity> groups = privilegeEntity.getGroupEntities();
            for(GroupEntity entity : groups)
            {
                entity.revokePrivilege(privilegeEntity.getId());
            }
            getGroupRepository().saveAll(groups);

            getRepository().deleteById(id);
            return true;
        }).orElse(false);
    }
}
