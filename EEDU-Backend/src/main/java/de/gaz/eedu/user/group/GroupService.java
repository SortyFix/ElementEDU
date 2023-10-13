package de.gaz.eedu.user.group;

import de.gaz.eedu.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.exception.NameOccupiedException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class GroupService implements EntityService<GroupEntity, GroupModel, GroupCreateModel> {

    @Getter(AccessLevel.PROTECTED)
    private final GroupRepository groupRepository;

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
        getGroupRepository().findByName(createModel.name()).map(toModel()).ifPresent(occupiedModel ->
        {
            throw new NameOccupiedException(occupiedModel.name());
        });

        return getGroupRepository().save(createModel.toEntity());
    }

    @Override public boolean delete(long id)
    {
        return getGroupRepository().findById(id).map(userEntity ->
        {
            getGroupRepository().deleteById(id);
            return true;
        }).orElse(false);
    }

    @Override
    public @NotNull GroupEntity saveEntity(@NotNull GroupEntity entity) {
        return getGroupRepository().save(entity);
    }

    @Override
    @Contract(pure = true)
    public @NotNull Function<GroupModel, GroupEntity> toEntity() {
        return groupModel -> new GroupEntity(groupModel.id(), groupModel.name(), groupModel.userEntities(), groupModel.privilegeEntities());
    }

    @Override
    @Contract(pure = true)
    public @NotNull Function<GroupEntity, GroupModel> toModel() {
        return groupEntity -> new GroupModel(groupEntity.getId(), groupEntity.getName(), groupEntity.getUsers(), groupEntity.getPrivileges());
    }

}