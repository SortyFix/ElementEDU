package de.gaz.eedu.user.privileges;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.GroupService;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.privileges.model.PrivilegeCreateModel;
import de.gaz.eedu.user.privileges.model.PrivilegeModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service @AllArgsConstructor @Getter(AccessLevel.PROTECTED) public class PrivilegeService extends EntityService<PrivilegeRepository, PrivilegeEntity, PrivilegeModel, PrivilegeCreateModel>
{

    @Getter(AccessLevel.NONE)
    private final PrivilegeRepository privilegeRepository;
    private final GroupService groupService;

    @Override
    public @NotNull PrivilegeRepository getRepository()
    {
        return privilegeRepository;
    }

    @Override public @NotNull PrivilegeEntity[] createEntity(@NotNull PrivilegeCreateModel... createModel) throws CreationException
    {
        List<String> loginNames = Arrays.stream(createModel).map(PrivilegeCreateModel::name).toList();
        boolean duplicates = loginNames.size() != loginNames.stream().collect(Collectors.toUnmodifiableSet()).size();

        if(duplicates || getRepository().existsByNameIn(loginNames))
        {
            throw new OccupiedException();
        }

        PrivilegeEntity[] entities = new PrivilegeEntity[createModel.length];
        Function<PrivilegeCreateModel, PrivilegeEntity> toEntity = current -> current.toEntity(new PrivilegeEntity());
        return getRepository().saveAll(Stream.of(createModel).map(toEntity).collect(Collectors.toList())).toArray(entities);
    }

    @Override public void deleteRelations(@NotNull PrivilegeEntity entry)
    {
        Set<GroupEntity> groups = entry.getGroupEntities();
        groups.forEach(group -> group.revokePrivilege(getGroupService(), entry.getId()));
    }
}
