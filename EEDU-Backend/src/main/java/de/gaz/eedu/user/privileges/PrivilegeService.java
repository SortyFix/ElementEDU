package de.gaz.eedu.user.privileges;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.GroupService;
import de.gaz.eedu.user.privileges.model.PrivilegeCreateModel;
import de.gaz.eedu.user.privileges.model.PrivilegeModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service @AllArgsConstructor @Getter(AccessLevel.PROTECTED) public class PrivilegeService implements EntityService<PrivilegeRepository, PrivilegeEntity, PrivilegeModel, PrivilegeCreateModel>
{

    @Getter(AccessLevel.NONE)
    private final PrivilegeRepository privilegeRepository;
    private final GroupService groupService;

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
            groups.forEach(group -> group.revokePrivilege(getGroupService(), privilegeEntity.getId()));

            getRepository().deleteById(id);

            String deleteMessage = "The privilege {} has been deleted and disassociated it from {} groups";
            LoggerFactory.getLogger(PrivilegeService.class).info(deleteMessage, privilegeEntity.getId(), groups.size());
            return true;
        }).orElse(false);
    }
}
