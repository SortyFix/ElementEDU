package de.gaz.sp.user.group;

import de.gaz.sp.user.exception.NameOccupiedException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class GroupService {

    @Getter(AccessLevel.PROTECTED) private final GroupRepository groupRepository;

    public Optional<GroupModel> loadGroupByID(@NotNull Long id)
    {
        return getGroupRepository().findById(id).map(toModel());
    }

    public @NotNull Optional<GroupModel> loadGroupByName(@NotNull String name)
    {
        return getGroupRepository().findByName(name).map(toModel());
    }

    public @NotNull GroupModel createGroup(@NotNull GroupModel groupModel)
    {
        getGroupRepository().findByName(groupModel.name()).map(toModel()).ifPresent(model ->
        {
            throw new NameOccupiedException(model.name());
        });

        return toModel().apply(groupRepository.save(toGroup().apply(groupModel)));
    }

    @Contract(pure = true)
    private @NotNull Function<GroupModel, GroupEntity> toGroup()
    {
        return groupModel -> new GroupEntity(groupModel.id(), groupModel.name(), groupModel.userEntities(), groupModel.privilegeEntities());
    }

    @Contract(pure = true)
    private @NotNull Function<GroupEntity, GroupModel> toModel()
    {
        return groupEntity -> new GroupModel(groupEntity.getId(), groupEntity.getName(), groupEntity.getUserEntities(), groupEntity.getPrivilegeEntities());
    }
}
