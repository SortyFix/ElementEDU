package de.gaz.eedu.user.privileges;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.group.GroupService;
import de.gaz.eedu.user.privileges.model.PrivilegeCreateModel;
import de.gaz.eedu.user.privileges.model.PrivilegeModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * Controller for managing privilege-related operations for user groups.
 * <p>
 * This controller provides endpoints for creating, deleting, retrieving, granting, and revoking privileges.
 * <p>
 * Accessible only to users with appropriate privileges as specified for each endpoint.
 *
 * @author Ivo Quiring
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
@RequestMapping("/api/v1/user/group/privilege")
public class PrivilegeController extends EntityController<String, PrivilegeService, PrivilegeModel, PrivilegeCreateModel>
{
    private final PrivilegeService service;

    /**
     * Handles the creation of a new privilege.
     * <p>
     * This method processes requests to create a new privilege using the provided {@link PrivilegeCreateModel}.
     * Upon successful creation, a {@link ResponseEntity} containing the created {@link PrivilegeModel} is returned.
     * This endpoint is restricted to users with appropriate privileges.
     * <p>
     * This endpoint is accessible only to users with any of the following authorities: {@code ${privilege.privilege.create}}
     * or {@code ${privilege.privilege.all}}.
     *
     * @param model the {@link PrivilegeCreateModel} containing details for the new privilege, provided in the request body.
     *              Must not be null.
     * @return a {@link ResponseEntity} containing the created {@link PrivilegeModel} upon successful creation.
     */
    @PreAuthorize("hasAuthority('PRIVILEGE_CREATE')") @PostMapping("/create") @Override
    public @NotNull ResponseEntity<PrivilegeModel[]> create(@NotNull @RequestBody PrivilegeCreateModel[] model) throws CreationException
    {
        return super.create(model);
    }

    /**
     * Handles the deletion of a privilege.
     * <p>
     * This method processes requests to delete a privilege specified by the provided {@code id}. The deletion
     * is performed only if the user has the required authority. This endpoint is restricted to users with
     * appropriate privileges.
     * <p>
     * This endpoint is accessible only to users with any of the following authorities: {@code ${privilege.privilege.delete}}
     * or {@code ${privilege.privilege.all}}.
     *
     * @param id the unique identifier of the privilege to be deleted, provided as a path variable. Must not be null.
     * @return {@code true} if the privilege was successfully deleted; otherwise, {@code false}.
     */
    @PreAuthorize("hasAuthority('PRIVILEGE_DELETE')") @DeleteMapping("/delete/{id}") @Override
    public @NotNull ResponseEntity<Void> delete(@PathVariable @NotNull String[] id)
    {
        return super.delete(id);
    }

    /**
     * Retrieves data for a specific privilege.
     * <p>
     * This method processes requests to retrieve information about a privilege specified by the provided {@code id}.
     * The requested privilege's details are returned in a {@link ResponseEntity} containing the {@link PrivilegeModel}.
     * This endpoint is restricted to users with appropriate privileges.
     * <p>
     * This endpoint is accessible only to users with any of the following authorities: {@code ${privilege.privilege.get}}
     * or {@code ${privilege.privilege.all}}.
     *
     * @param id the unique identifier of the privilege to retrieve, provided as a path variable. Must not be null.
     * @return a {@link ResponseEntity} containing the {@link PrivilegeModel} of the specified privilege.
     */
    @PreAuthorize("hasAuthority('PRIVILEGE_GET')") @GetMapping("/get/{id}") @Override
    public @NotNull ResponseEntity<PrivilegeModel> getData(@PathVariable @NotNull String id)
    {
        return super.getData(id);
    }

    /**
     * Grants privileges to a specified group.
     * <p>
     * This method processes requests to grant one or more privileges to a specified group, identified by the provided
     * {@code group} ID. The privilege IDs are provided in the request body, and the specified privileges are granted
     * to the group upon successful processing. This endpoint is restricted to users with appropriate privileges.
     * <p>
     * This endpoint is accessible only to users with any of the following authorities: {@code ${privilege.group.privilege.grant}}
     * or {@code ${privilege.group.all}}.
     *
     * @param group      the unique identifier of the target group, provided as a path variable.
     * @param privileges an array of privilege IDs to grant to the specified group, provided in the request body.
     *                   Must not be null.
     */
    @PreAuthorize("hasAuthority('GROUP_PRIVILEGE_GRANT')") @PutMapping("/{group}/grant/{privileges}")
    public @NotNull ResponseEntity<Void> grantPrivileges(@PathVariable String group, @PathVariable @NotNull String[] privileges)
    {
        log.info("Received incoming request for granting privilege(s) {} to group {}.", privileges, group);

        PrivilegeEntity[] entities = getService().loadEntityById(Arrays.asList(privileges)).toArray(PrivilegeEntity[]::new);
        GroupService groupService = getService().getGroupService();

        boolean modified = groupService.loadEntityByIDSafe(group).grantPrivilege(groupService, entities);
        return empty(modified ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
    }

    /**
     * Revokes privileges from a specified group.
     * <p>
     * This method processes requests to revoke one or more privileges from a specified group, identified by the
     * provided {@code group} ID. The privilege IDs are provided in the request body, and the specified privileges
     * are revoked from the group upon successful processing. This endpoint is restricted to users with appropriate privileges.
     * <p>
     * This endpoint is accessible only to users with any of the following authorities: {@code ${privilege.group.privilege.revoke}}
     * or {@code ${privilege.group.all}}.
     *
     * @param group      the unique identifier of the target group, provided as a path variable.
     * @param privileges an array of privilege IDs to revoke from the specified group, provided in the request body.
     *                   Must not be null.
     */
    @PreAuthorize("hasAuthority('GROUP_PRIVILEGE_REVOKE')") @DeleteMapping("/{group}/revoke/{privileges}")
    public @NotNull ResponseEntity<Void> revokePrivileges(@PathVariable String group, @PathVariable @NotNull String[] privileges)
    {
        log.info("Received incoming request for revoking privilege(s) {} to group {}.", privileges, group);

        GroupService groupService = getService().getGroupService();
        boolean modified = groupService.loadEntityByIDSafe(group).revokePrivilege(groupService, privileges);
        return empty(modified ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
    }
}
