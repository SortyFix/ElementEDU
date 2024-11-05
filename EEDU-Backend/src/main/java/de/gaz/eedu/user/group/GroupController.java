package de.gaz.eedu.user.group;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.group.model.GroupCreateModel;
import de.gaz.eedu.user.group.model.GroupModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
@RestController
@RequestMapping("/api/v1/user/group")
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
@Slf4j
public class GroupController extends EntityController<GroupService, GroupModel, GroupCreateModel>
{
    private final GroupService service;

    /**
     * Handles the attachment of groups to a specified user.
     * <p>
     * This method processes requests to attach one or more groups to a specified user, identified by the provided
     * {@code userid}. The groupIds are provided in the request body, and the groups are loaded and attached to
     * the user upon successful processing. This endpoint is restricted to users with appropriate privileges.
     * <p>
     * This endpoint is accessible only to users with any of the following authorities: {@code ${privilege.user.group.attach}}
     * or {@code ${privilege.user.all}}.
     *
     * @param user   the unique identifier of the target user, provided as a path variable.
     * @param groups an array of group IDs to attach to the specified user, provided in the request body.
     *               Must not be null.
     */
    @PreAuthorize("@verificationService.hasAuthority('privilege.user.group.attach')")
    @PostMapping("/{user}/attach")
    public void attachGroups(@PathVariable long user, @RequestBody @NotNull Long... groups)
    {
        log.info("Received incoming request for attaching group(s) {} to user {}.", groups, user);

        GroupEntity[] entities = getService().loadEntityById(groups).toArray(GroupEntity[]::new);
        UserService userService = getService().getUserService();
        userService.loadEntityByIDSafe(user).attachGroups(userService, entities);
    }

    /**
     * Handles the detachment of groups from a specified user.
     * <p>
     * This method processes requests to detach one or more groups from a specified user, identified by the
     * provided {@code userid}. The groupIds are provided in the request body, and the specified groups are
     * detached from the user upon successful processing. This endpoint is restricted to users with appropriate privileges.
     * <p>
     * This endpoint is accessible only to users with any of the following authorities: {@code ${privilege.user.group.detach}}
     * or {@code ${privilege.user.all}}.
     *
     * @param user   the unique identifier of the target user, provided as a path variable.
     * @param groups an array of group IDs to detach from the specified user, provided in the request body.
     *               Must not be null.
     */
    @PreAuthorize("@verificationService.hasAuthority('privilege.user.group.detach')")
    @PostMapping("/{user}/detach")
    public void detachGroups(@PathVariable long user, @RequestBody @NotNull Long... groups)
    {
        log.info("Received incoming request for detaching group(s) {} to user {}.", groups, user);

        UserService userService = getService().getUserService();
        userService.loadEntityByIDSafe(user).detachGroups(userService, groups);
    }

    /**
     * Handles the creation of a new group.
     * <p>
     * This method processes requests to create a new group using the provided {@link GroupCreateModel}. Upon
     * successful creation, a {@link ResponseEntity} containing the created {@link GroupModel} is returned. This
     * endpoint is restricted to users with appropriate privileges.
     * <p>
     * This endpoint is accessible only to users with any of the following authorities: {@code ${privilege.group.create}}
     * or {@code ${privilege.group.all}}.
     *
     * @param model the {@link GroupCreateModel} containing details for the new group, provided in the request body.
     *              Must not be null.
     * @return a {@link ResponseEntity} containing the created {@link GroupModel} upon successful creation.
     * @throws CreationException if an error occurs during the creation of the group.
     */
    @PreAuthorize("@verificationService.hasAuthority('privilege.group.create')") @PostMapping("/create")
    @Override
    public @NotNull ResponseEntity<GroupModel[]> create(@NotNull @RequestBody GroupCreateModel[] model) throws CreationException
    {
        return super.create(model);
    }

    /**
     * Handles the deletion of a group.
     * <p>
     * This method processes requests to delete a group specified by the provided {@code id}. The deletion
     * is performed only if the user has the required authority. This endpoint is restricted to users with
     * appropriate privileges.
     * <p>
     * This endpoint is accessible only to users with any of the following authorities: {@code ${privilege.group.delete}}
     * or {@code ${privilege.group.all}}.
     *
     * @param id the unique identifier of the group to be deleted, provided as a path variable. Must not be null.
     * @return {@code true} if the group was successfully deleted; otherwise, {@code false}.
     */
    @PreAuthorize("@verificationService.hasAuthority('privilege.group.delete')") @DeleteMapping("/delete/{id}")
    @Override public @NotNull Boolean delete(@PathVariable @NotNull Long id)
    {
        return super.delete(id);
    }

    /**
     * Retrieves data for a specific group.
     * <p>
     * This method processes requests to retrieve information about a group specified by the provided {@code id}.
     * The requested group's details are returned in a {@link ResponseEntity} containing the {@link GroupModel}.
     * This endpoint is restricted to users with appropriate privileges.
     * <p>
     * This endpoint is accessible only to users with any of the following authorities: {@code ${privilege.group.get}}
     * or {@code ${privilege.group.all}}.
     *
     * @param id the unique identifier of the group to retrieve, provided as a path variable. Must not be null.
     * @return a {@link ResponseEntity} containing the {@link GroupModel} of the specified group.
     */
    @PreAuthorize("@verificationService.hasAuthority('privilege.group.get')") @GetMapping("/get/{id}") @Override
    public @NotNull ResponseEntity<GroupModel> getData(@PathVariable @NotNull Long id)
    {
        return super.getData(id);
    }
}
