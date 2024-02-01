package de.gaz.eedu.user.group;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter(AccessLevel.PROTECTED)
@Service
@AllArgsConstructor
public class GroupService implements EntityService<GroupEntity, GroupModel, GroupCreateModel> {

    private final static Logger LOGGER = LoggerFactory.getLogger(GroupService.class);
    private final GroupRepository groupRepository;
    private final UserService userService; // managed by
    private final PrivilegeRepository privilegeRepository;

    @Override
    public @NotNull Optional<GroupEntity> loadEntityByID(long id) {
        return getGroupRepository().findById(id);
    }

    @Override
    public @NotNull Optional<GroupEntity> loadEntityByName(@NotNull String name) {
        return getGroupRepository().findByName(name);
    }

    @Override
    public @Unmodifiable @NotNull List<GroupEntity> findAllEntities() {
        return getGroupRepository().findAll();
    }

    @Override
    public @NotNull GroupEntity createEntity(@NotNull GroupCreateModel createModel) throws CreationException {
        getGroupRepository().findByName(createModel.name()).map(toModel()).ifPresent(occupiedModel -> {
            throw new NameOccupiedException(occupiedModel.name());
        });

        Set<UserEntity> users = Stream.of(createModel.users()).map(getUserService()::loadEntityByID).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
        GroupEntity groupEntity = getGroupRepository().save(createModel.toEntity(new GroupEntity(users), group -> {
            Stream<Optional<PrivilegeEntity>> privileges = Stream.of(createModel.privileges()).map(getPrivilegeRepository()::findById);
            group.setPrivileges(privileges.map(privilegeEntity -> privilegeEntity.orElse(null)).collect(Collectors.toSet()));
            return group;
        }));

        users.forEach(user -> user.attachGroups(getUserService(), groupEntity)); // attach users to this group
        return groupEntity;
    }

    @Override
    public boolean delete(long id) {
        return getGroupRepository().findById(id).map(groupEntity -> {

            // Delete this entity from the users
            Set<UserEntity> users = groupEntity.getUsers();
            users.forEach(user -> user.detachGroups(groupEntity.getId()));
            getUserService().saveEntity(users);

            // Delete privileges from this entity
            Long[] privilegeIDs = groupEntity.getPrivileges().stream().map(PrivilegeEntity::getId).toArray(Long[]::new);
            groupEntity.revokePrivilege(this, privilegeIDs);

            getGroupRepository().deleteById(id);
            return true;
        }).orElse(false);
    }

    @Override
    public <T extends GroupEntity> @NotNull List<T> saveEntity(@NotNull Iterable<T> entity)
    {
        return getGroupRepository().saveAll(entity);
    }

    @Transactional
    @Override
    @Contract(pure = true)
    public @NotNull Function<GroupModel, GroupEntity> toEntity() {
        return groupModel -> loadEntityByID(groupModel.id()).orElseThrow(() -> new EntityUnknownException(groupModel.id()));
    }

    @Override
    @Contract(pure = true)
    public @NotNull Function<GroupEntity, GroupModel> toModel() {
        return GroupEntity::toModel;
    }
}
