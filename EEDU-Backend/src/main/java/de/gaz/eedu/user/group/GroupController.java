package de.gaz.eedu.user.group;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.user.group.model.GroupCreateModel;
import de.gaz.eedu.user.group.model.GroupModel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController extends EntityController<GroupService,
        GroupModel, GroupCreateModel>
{

    private final GroupService groupService;

    @Override
    protected @NotNull GroupService getEntityService()
    {
        return groupService;
    }

    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/create") @Override public @NotNull ResponseEntity<GroupModel> create(@NotNull @RequestBody GroupCreateModel model)
    {
        return super.create(model);
    }

    @PreAuthorize("isAuthenticated()") @DeleteMapping("/delete/{id}") @Override public @NotNull Boolean delete(@PathVariable @NotNull Long id)
    {
        return super.delete(id);
    }

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/{id}") @Override public @NotNull ResponseEntity<GroupModel> getData(@PathVariable @NotNull Long id)
    {
        return super.getData(id);
    }
}
