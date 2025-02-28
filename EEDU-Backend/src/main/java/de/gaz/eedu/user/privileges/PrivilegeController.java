package de.gaz.eedu.user.privileges;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.StateTransitionException;
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
     * Endpoint for granting privileges to groups.
     * <p>
     * In order to call this the authentication principle needs the privilege {@link SystemPrivileges#GROUP_PRIVILEGE_GRANT}.
     *
     * @param group      the name of the group to which privileges will be granted.
     * @param privileges an array of privileges to grant to the specified group. For example: "READ,WRITE".
     * @return A {@link ResponseEntity} with an HTTP status indicating the result of the operation.
     * Returns {@link HttpStatus#OK} if the privileges were successfully granted.
     * Returns {@link HttpStatus#NOT_MODIFIED} if the privileges were not modified (e.g., the group already
     * had the specified privileges, or the operation failed for some other reason).
     * @throws StateTransitionException is thrown when the group already has one of the privileges
     * @see SystemPrivileges#GROUP_PRIVILEGE_GRANT
     */
    @PutMapping("/{group}/grant/{privileges}")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).GROUP_PRIVILEGE_GRANT.toString())")
    public @NotNull ResponseEntity<Void> grantPrivileges(@PathVariable String group, @PathVariable @NotNull String[] privileges) throws StateTransitionException
    {
        log.info("Received incoming request for granting privilege(s) {} to group {}.", privileges, group);
        return empty(getService().grantPrivileges(group, privileges) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
    }

    /**
     * Endpoint for revoking privileges from groups.
     * <p>
     * In order to call this the authentication principle needs the privilege {@link SystemPrivileges#GROUP_PRIVILEGE_REVOKE}.
     * <p>
     * Note! This is using path variables. it should be formatted like
     *
     * @param group      the name of the group from which privileges will be revoked.
     * @param privileges an array of privileges to revoked from the specified group. For example: "READ,WRITE".
     * @return A {@link ResponseEntity} with an HTTP status indicating the result of the operation.
     * Returns {@link HttpStatus#OK} if the privileges were successfully revoked.
     * Returns {@link HttpStatus#NOT_MODIFIED} if the privileges were not modified (e.g., the group
     * had none of the specified privileges, or the operation failed for some other reason).
     * @see SystemPrivileges#GROUP_PRIVILEGE_REVOKE
     */
    @DeleteMapping("/{group}/revoke/{privileges}")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).GROUP_PRIVILEGE_REVOKE.toString())")
    public @NotNull ResponseEntity<Void> revokePrivileges(@PathVariable String group, @PathVariable @NotNull String[] privileges)
    {
        log.info("Received incoming request for revoking privilege(s) {} to group {}.", privileges, group);
        return empty(getService().revokePrivileges(group, privileges) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).PRIVILEGE_CREATE.toString())")
    @Override
    public @NotNull ResponseEntity<PrivilegeModel[]> create(@NotNull @RequestBody PrivilegeCreateModel[] model) throws CreationException
    {
        return super.create(model);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).PRIVILEGE_DELETE.toString())")
    @Override public @NotNull ResponseEntity<Void> delete(@PathVariable @NotNull String[] id)
    {
        return super.delete(id);
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).PRIVILEGE_GET.toString())")
    @Override public @NotNull ResponseEntity<PrivilegeModel> getData(@PathVariable @NotNull String id)
    {
        return super.getData(id);
    }
}
