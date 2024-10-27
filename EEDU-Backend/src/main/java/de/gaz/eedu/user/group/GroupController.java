package de.gaz.eedu.user.group;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.group.model.GroupCreateModel;
import de.gaz.eedu.user.group.model.GroupModel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/group")
@RequiredArgsConstructor
public class GroupController extends EntityController<GroupService, GroupModel, GroupCreateModel>
{
    private final GroupService groupService;

    @Override protected @NotNull GroupService getEntityService()
    {
        return groupService;
    }

    @PreAuthorize("hasAuthority(${privilege.user.group.attach})") @PostMapping("/{user}/attach")
    public void attachGroups(@PathVariable long user, @RequestBody @NotNull Long... groups)
    {
        GroupEntity[] entities = getEntityService().loadEntityById(groups).toArray(GroupEntity[]::new);
        UserService userService = getEntityService().getUserService();
        userService.loadEntityByIDSafe(user).attachGroups(userService, entities);
    }

    @PreAuthorize("hasAuthority(${privilege.user.group.detach})") @PostMapping("/{user}/detach")
    public void detachGroups(@PathVariable long user, @RequestBody @NotNull Long... groups)
    {
        UserService userService = getEntityService().getUserService();
        userService.loadEntityByIDSafe(user).detachGroups(userService, groups);
    }

    @PreAuthorize("hasAuthority(${privilege.group.create})") @PostMapping("/create") @Override
    public @NotNull ResponseEntity<GroupModel> create(@NotNull @RequestBody GroupCreateModel model) throws CreationException
    {
        return super.create(model);
    }

    @PreAuthorize("hasAuthority(${privilege.group.delete})") @DeleteMapping("/delete/{id}") @Override
    public @NotNull Boolean delete(@PathVariable @NotNull Long id)
    {
        return super.delete(id);
    }

    @PreAuthorize("hasAuthority(${privilege.group.get})") @GetMapping("/get/{id}") @Override
    public @NotNull ResponseEntity<GroupModel> getData(@PathVariable @NotNull Long id)
    {
        return super.getData(id);
    }
}
