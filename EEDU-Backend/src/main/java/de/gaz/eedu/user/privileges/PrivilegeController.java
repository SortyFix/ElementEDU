package de.gaz.eedu.user.privileges;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.user.privileges.model.PrivilegeCreateModel;
import de.gaz.eedu.user.privileges.model.PrivilegeModel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/privilege") @RequiredArgsConstructor
public class PrivilegeController extends EntityController<PrivilegeService, PrivilegeModel, PrivilegeCreateModel>
{

    private final PrivilegeService privilegeService;

    @Override
    protected @NotNull PrivilegeService getService()
    {
        return privilegeService;
    }

    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/create") @Override public @NotNull ResponseEntity<PrivilegeModel[]> create(@NotNull @RequestBody PrivilegeCreateModel[] model)
    {
        return super.create(model);
    }

    @PreAuthorize("isAuthenticated()") @DeleteMapping("/delete/{id}") @Override public @NotNull Boolean delete(@PathVariable @NotNull Long id)
    {
        return super.delete(id);
    }

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/{id}") @Override public @NotNull ResponseEntity<PrivilegeModel> getData(@PathVariable @NotNull Long id)
    {
        return super.getData(id);
    }
}
