package de.gaz.eedu.user.group;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.AccountType;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.group.model.GroupCreateModel;
import de.gaz.eedu.user.group.model.GroupModel;
import de.gaz.eedu.user.group.repository.GroupRepository;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import de.gaz.eedu.user.privileges.PrivilegeRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class GroupService extends EntityService<String, GroupRepository, GroupEntity, GroupModel, GroupCreateModel>
{

    private final GroupRepository repository;
    private final UserService userService; // managed by
    private final PrivilegeRepository privilegeRepository;

    private static void validateGroups(String @NotNull [] entities) throws ResponseStatusException
    {
        Set<String> specialGroups = AccountType.groupSet();
        for (String entity : entities)
        {
            if (!specialGroups.contains(entity))
            {
                continue;
            }

            throw new GroupUnprocessableException(entity);
        }
    }

    @Override
    public @NotNull List<GroupEntity> createEntity(@NotNull Set<GroupCreateModel> createModel) throws CreationException
    {
        if (getRepository().existsByIdIn(createModel.stream().map(GroupCreateModel::id).toList()))
        {
            throw new OccupiedException();
        }

        return getRepository().saveAll(createModel.stream().map(current ->
        {
            GroupEntity groupEntity = new GroupEntity(current.id());
            return current.toEntity(groupEntity, group ->
            {
                List<String> ids = Arrays.asList(current.privileges());
                group.grantPrivilege(getPrivilegeRepository().findAllById(ids).toArray(PrivilegeEntity[]::new));
                return group;
            });
        }).toList());
    }

    @Override public void deleteRelations(@NotNull GroupEntity entry)
    {
        // Delete this entity from the users
        Set<UserEntity> users = entry.getUsers();
        users.forEach(user -> user.detachGroups(entry.getId()));
        getUserService().saveEntity(users);
    }

    @Override public @NotNull @Unmodifiable Set<GroupEntity> findAllEntities(@NotNull Predicate<GroupEntity> predicate)
    {
        return getRepository().findAllEntities().stream().filter(predicate).collect(Collectors.toUnmodifiableSet());
    }

    @Transactional public boolean attachGroups(long userId, @NotNull String[] groups) throws GroupUnprocessableException
    {
        validateGroups(groups);
        GroupEntity[] entities = loadEntityById(Arrays.asList(groups)).toArray(GroupEntity[]::new);
        return getUserService().loadEntityByIDSafe(userId).attachGroups(getUserService(), entities);
    }

    @Transactional public boolean detachGroups(long userId, @NotNull String[] groups) throws GroupUnprocessableException
    {
        validateGroups(groups);
        return getUserService().loadEntityByIDSafe(userId).detachGroups(getUserService(), groups);
    }
}
