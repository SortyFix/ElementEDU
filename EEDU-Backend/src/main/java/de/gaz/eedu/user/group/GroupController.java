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

    @PreAuthorize("hasAnyAuthority(${privilege.user.group.attach}, ${privilege.user.all})")
    @PostMapping("/{user}/attach")
    public void attachGroups(@PathVariable long user, @RequestBody @NotNull Long... groups)
    {
        GroupEntity[] entities = getEntityService().loadEntityById(groups).toArray(GroupEntity[]::new);
        UserService userService = getEntityService().getUserService();
        userService.loadEntityByIDSafe(user).attachGroups(userService, entities);
    }

    @PreAuthorize("hasAnyAuthority(${privilege.user.group.detach}, ${privilege.user.all})")
    @PostMapping("/{user}/detach")
    public void detachGroups(@PathVariable long user, @RequestBody @NotNull Long... groups)
    {
        UserService userService = getEntityService().getUserService();
        userService.loadEntityByIDSafe(user).detachGroups(userService, groups);
    }

    @PreAuthorize("hasAnyAuthority(${privilege.group.create}, ${privilege.group.all})") @PostMapping("/create")
    @Override
    public @NotNull ResponseEntity<GroupModel> create(@NotNull @RequestBody GroupCreateModel model) throws CreationException
    {
        return super.create(model);
    }

    @PreAuthorize("hasAnyAuthority(${privilege.group.delete}, ${privilege.group.all})") @DeleteMapping("/delete/{id}")
    @Override public @NotNull Boolean delete(@PathVariable @NotNull Long id)
    {
        return super.delete(id);
    }

    @PreAuthorize("hasAnyAuthority(${privilege.group.get}, ${privilege.group.all})") @GetMapping("/get/{id}") @Override
    public @NotNull ResponseEntity<GroupModel> getData(@PathVariable @NotNull Long id)
    {
        return super.getData(id);
    }
}
