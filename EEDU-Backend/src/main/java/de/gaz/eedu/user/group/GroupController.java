package de.gaz.eedu.user.group;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.group.model.GroupCreateModel;
import de.gaz.eedu.user.group.model.GroupModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Controller for managing group-related operations for users.
 * <p>
 * This controller provides endpoints for creating, deleting, retrieving, attaching, detaching, granting, and
 * revoking privileges for groups.
 * <p>
 * Accessible only to users with appropriate privileges as specified for each endpoint.
 *
 * @author Ivo Quiring
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
@RequestMapping("/api/v1/user/group")
public class GroupController extends EntityController<String, GroupService, GroupModel, GroupCreateModel>
{
    private final GroupService service;

    @PutMapping("/{user}/attach/{groups}")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).USER_GROUP_ATTACH.toString())")
    public @NotNull ResponseEntity<Void> attachGroups(@PathVariable long user, @PathVariable @NotNull String... groups)
    {
        log.info("Received incoming request for attaching group(s) {} to user {}.", groups, user);
        return empty(getService().attachGroups(user, groups) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
    }


    @DeleteMapping("/{user}/detach/{groups}")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).USER_GROUP_DETACH.toString())")
    public @NotNull ResponseEntity<Void> detachGroups(@PathVariable long user, @PathVariable @NotNull String... groups)
    {
        log.info("Received incoming request for detaching group(s) {} to user {}.", groups, user);
        return empty(getService().detachGroups(user, groups) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).GROUP_CREATE.toString())") @Override
    public @NotNull ResponseEntity<GroupModel[]> create(@NotNull @RequestBody GroupCreateModel[] model) throws CreationException
    {
        return super.create(model);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).GROUP_DELETE.toString())") @Override
    public @NotNull ResponseEntity<Void> delete(@PathVariable @NotNull String[] id) {return super.delete(id);}

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).GROUP_GET.toString())") @Override
    public @NotNull ResponseEntity<GroupModel> getData(@PathVariable @NotNull String id) {return super.getData(id);}

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).GROUP_GET.toString())") @Override
    public @NotNull ResponseEntity<Set<GroupModel>> fetchAll() {return super.fetchAll();}
}
