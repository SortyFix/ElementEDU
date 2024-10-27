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

/**
 * Controller for managing privilege-related operations for user groups.
 * <p>
 * This controller provides endpoints for creating, deleting, retrieving, granting, and revoking privileges.
 * <p>
 * Accessible only to users with appropriate privileges as specified for each endpoint.
 *
 * @author Ivo Quiring
 */
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
    @PreAuthorize("hasAnyAuthority(${privilege.privilege.create}, ${privilege.privilege.all})") @PostMapping("/create")
    @Override
    public @NotNull ResponseEntity<PrivilegeModel> create(@NotNull @RequestBody PrivilegeCreateModel model) throws CreationException
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
    @PreAuthorize("hasAnyAuthority(${privilege.privilege.delete}, ${privilege.privilege.all})")
    @DeleteMapping("/delete/{id}") @Override public @NotNull Boolean delete(@PathVariable @NotNull Long id)
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
    @PreAuthorize("hasAnyAuthority(${privilege.privilege.get}, ${privilege.privilege.all})") @GetMapping("/get/{id}")
    @Override public @NotNull ResponseEntity<PrivilegeModel> getData(@PathVariable @NotNull Long id)
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
    @PreAuthorize("hasAnyAuthority(${privilege.group.privilege.grant}, ${privilege.group.all})")
    @PostMapping("/{group}/grant")
    public void grantPrivileges(@PathVariable long group, @RequestBody @NotNull Long... privileges)
    {
        PrivilegeEntity[] entities = getEntityService().loadEntityById(privileges).toArray(PrivilegeEntity[]::new);
        GroupService groupService = getEntityService().getGroupService();
        groupService.loadEntityByIDSafe(group).grantPrivilege(groupService, entities);
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
    @PreAuthorize("hasAnyAuthority(${privilege.group.privilege.revoke}, ${privilege.group.all})")
    @PostMapping("/{group}/revoke")
    public void revokePrivileges(@PathVariable long group, @RequestBody @NotNull Long... privileges)
    {
        GroupService groupService = getEntityService().getGroupService();
        groupService.loadEntityByIDSafe(group).revokePrivilege(groupService, privileges);
    }
}
