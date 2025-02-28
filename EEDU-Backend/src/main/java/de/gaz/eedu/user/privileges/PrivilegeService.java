package de.gaz.eedu.user.privileges;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.group.GroupService;
import de.gaz.eedu.user.privileges.model.PrivilegeCreateModel;
import de.gaz.eedu.user.privileges.model.PrivilegeModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class PrivilegeService extends EntityService<String, PrivilegeRepository, PrivilegeEntity, PrivilegeModel, PrivilegeCreateModel>
{
    private final PrivilegeRepository repository;
    private final GroupService groupService;

    @Transactional
    public boolean grantPrivileges(@PathVariable String group, @PathVariable @NotNull String[] privileges)
    {
        PrivilegeEntity[] entities = loadEntityById(Arrays.asList(privileges)).toArray(PrivilegeEntity[]::new);
        return getGroupService().loadEntityByIDSafe(group).grantPrivilege(getGroupService(), entities);
    }

    @Transactional
    public boolean revokePrivileges(@PathVariable String group, @PathVariable @NotNull String[] privileges)
    {
        return getGroupService().loadEntityByIDSafe(group).revokePrivilege(getGroupService(), privileges);
    }

    @Transactional
    @Override
    public @NotNull List<PrivilegeEntity> createEntity(@NotNull Set<PrivilegeCreateModel> createModel) throws CreationException
    {
        if (getRepository().existsByIdIn(createModel.stream().map(PrivilegeCreateModel::id).toList()))
        {
            throw new OccupiedException();
        }

        return saveEntity(createModel.stream().map(privilege ->
        {
            PrivilegeEntity privilegeEntity = new PrivilegeEntity(privilege.id());
            return privilege.toEntity(privilegeEntity);
        }).toList());
    }

    @Override public void deleteRelations(@NotNull PrivilegeEntity entry)
    {
        entry.getGroupEntities().forEach(group -> group.revokePrivilege(getGroupService(), entry.getId()));
    }
}
