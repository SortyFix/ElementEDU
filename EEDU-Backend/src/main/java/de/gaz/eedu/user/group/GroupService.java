package de.gaz.eedu.user.group;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.group.model.GroupCreateModel;
import de.gaz.eedu.user.group.model.GroupModel;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import de.gaz.eedu.user.privileges.PrivilegeRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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

    @Override
    public @NotNull GroupEntity createEntity(@NotNull GroupCreateModel createModel) throws CreationException {

        if(getRepository().existsByName(createModel.name()))
        {
            throw new NameOccupiedException(createModel.name());
        }

        Set<UserEntity> users = Stream.of(createModel.users()).map(getUserService()::loadEntityById).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
        GroupEntity groupEntity = getRepository().save(createModel.toEntity(new GroupEntity(users), group -> {
            Stream<Optional<PrivilegeEntity>> privileges = Stream.of(createModel.privileges()).map(getPrivilegeRepository()::findById);
            group.setPrivileges(privileges.map(privilegeEntity -> privilegeEntity.orElse(null)).collect(Collectors.toSet()));
            return group;
        }));

        users.forEach(user -> user.attachGroups(getUserService(), groupEntity)); // attach users to this group
        return groupEntity;
    }

    @Override public void deleteRelations(@NotNull GroupEntity entry)
    {
        // Delete this entity from the users
        Set<UserEntity> users = entry.getUsers();
        users.forEach(user -> user.detachGroups(entry.getId()));
        getUserService().saveEntity(users);
    }
}
