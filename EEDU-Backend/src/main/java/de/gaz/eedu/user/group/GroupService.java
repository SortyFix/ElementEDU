package de.gaz.eedu.user.group;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.group.model.GroupCreateModel;
import de.gaz.eedu.user.group.model.GroupModel;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import de.gaz.eedu.user.privileges.PrivilegeRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Getter(AccessLevel.PROTECTED)
@Service
@AllArgsConstructor
public class GroupService extends EntityService<GroupRepository, GroupEntity, GroupModel, GroupCreateModel> {

    @Getter(AccessLevel.NONE)
    private final GroupRepository groupRepository;
    private final UserService userService; // managed by
    private final PrivilegeRepository privilegeRepository;

    @Override
    public @NotNull GroupRepository getRepository()
    {
        return groupRepository;
    }

    @Override public @NotNull GroupEntity[] createEntity(@NotNull GroupCreateModel... createModel) throws CreationException
    {
        return getRepository().saveAll(Stream.of(createModel).map(current -> current.toEntity(new GroupEntity(), group ->
        {
            List<Long> ids = Arrays.asList(current.privileges());
            group.grantPrivilege(getPrivilegeRepository().findAllById(ids).toArray(PrivilegeEntity[]::new));
            return group;
        })).toList()).toArray(GroupEntity[]::new);
    }

    @Override public void deleteRelations(@NotNull GroupEntity entry)
    {
        // Delete this entity from the users
        Set<UserEntity> users = entry.getUsers();
        users.forEach(user -> user.detachGroups(entry.getId()));
        getUserService().saveEntity(users);
    }
}
