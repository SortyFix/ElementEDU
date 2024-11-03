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

import java.util.List;
import java.util.Set;

@Service @AllArgsConstructor @Getter(AccessLevel.PROTECTED) public class PrivilegeService extends EntityService<PrivilegeRepository, PrivilegeEntity, PrivilegeModel, PrivilegeCreateModel>
{

    @Getter(AccessLevel.PROTECTED)
    private final PrivilegeRepository repository;
    private final GroupService groupService;

    @Transactional
    @Override public @NotNull List<PrivilegeEntity> createEntity(@NotNull Set<PrivilegeCreateModel> createModel) throws CreationException
    {
        if (getRepository().existsByNameIn(createModel.stream().map(PrivilegeCreateModel::name).toList()))
        {
            throw new OccupiedException();
        }

        return saveEntity(createModel.stream().map(privilege -> privilege.toEntity(new PrivilegeEntity())).toList());
    }

    @Override public void deleteRelations(@NotNull PrivilegeEntity entry)
    {
        entry.getGroupEntities().forEach(group -> group.revokePrivilege(getGroupService(), entry.getId()));
    }
}
