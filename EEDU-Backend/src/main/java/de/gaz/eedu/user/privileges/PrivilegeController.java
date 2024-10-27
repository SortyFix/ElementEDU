package de.gaz.eedu.user.privileges;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.group.GroupService;
import de.gaz.eedu.user.privileges.model.PrivilegeCreateModel;
import de.gaz.eedu.user.privileges.model.PrivilegeModel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/group/privilege")
@RequiredArgsConstructor
public class PrivilegeController extends EntityController<PrivilegeService, PrivilegeModel, PrivilegeCreateModel>
{

    private final PrivilegeService privilegeService;

    @Override protected @NotNull PrivilegeService getEntityService()
    {
        return privilegeService;
    }

    @PreAuthorize("hasAnyAuthority(${privilege.privilege.create}, ${privilege.privilege.all})") @PostMapping("/create")
    @Override
    public @NotNull ResponseEntity<PrivilegeModel> create(@NotNull @RequestBody PrivilegeCreateModel model) throws CreationException
    {
        return super.create(model);
    }

    @PreAuthorize("hasAnyAuthority(${privilege.privilege.delete}, ${privilege.privilege.all})")
    @DeleteMapping("/delete/{id}") @Override public @NotNull Boolean delete(@PathVariable @NotNull Long id)
    {
        return super.delete(id);
    }

    @PreAuthorize("hasAnyAuthority(${privilege.privilege.get}, ${privilege.privilege.all})") @GetMapping("/get/{id}")
    @Override public @NotNull ResponseEntity<PrivilegeModel> getData(@PathVariable @NotNull Long id)
    {
        return super.getData(id);
    }

    @PreAuthorize("hasAnyAuthority(${privilege.group.privilege.grant}, ${privilege.group.all})") @PostMapping("/{group}/grant")
    public void grantPrivileges(@PathVariable long group, @RequestBody @NotNull Long... privileges)
    {
        PrivilegeEntity[] entities = getEntityService().loadEntityById(privileges).toArray(PrivilegeEntity[]::new);
        GroupService groupService = getEntityService().getGroupService();
        groupService.loadEntityByIDSafe(group).grantPrivilege(groupService, entities);
    }

    @PreAuthorize("hasAnyAuthority(${privilege.group.privilege.revoke}, ${privilege.group.all})") @PostMapping("/{group}/revoke")
    public void revokePrivileges(@PathVariable long group, @RequestBody @NotNull Long... privileges)
    {
        GroupService groupService = getEntityService().getGroupService();
        groupService.loadEntityByIDSafe(group).revokePrivilege(groupService, privileges);
    }
}
