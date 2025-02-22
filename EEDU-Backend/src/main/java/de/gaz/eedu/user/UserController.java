package de.gaz.eedu.user;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.model.ReducedUserModel;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.model.UserModel;
import de.gaz.eedu.user.verification.JwtTokenType;
import de.gaz.eedu.user.verification.model.AdvancedUserLoginModel;
import de.gaz.eedu.user.verification.model.UserLoginModel;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Set;

/**
 * Contains methods to interact with the {@link UserService} using a http request.
 * <p>
 * This is necessary to connect the backend to the frontend.
 * In this specific case the frontend can access this resource over {@code server.example.de/user}
 * <p>
 * Some examples are:<br>
 * - Creating a user<br>
 * - Deleting a user<br>
 * - Receive user data<br>
 * and some more...
 *
 * @author ivo
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/user")
@RequiredArgsConstructor
public class UserController extends EntityController<Long, UserService, UserModel, UserCreateModel>
{
    @Getter(AccessLevel.PROTECTED) private final UserService service;
    @Value("${development}") private final boolean development = false;
    private final UserService userService;

    /**
     * Creates a new user utilizing the provided {@link UserCreateModel}.
     * <p>
     * This method invokes {@code entityServices} to execute {@link UserService#create(java.util.Set)}.
     * If a user with the same longin id already exists, an {@link OccupiedException} is thrown.
     * <p>
     * Note that the invoking user must possess the "privilege.user.create" privilege,
     * which should be configured in the application's properties, to perform this action.
     *
     * @param model an instance of {@link UserCreateModel} containing the necessary user details.
     * @return a {@link ResponseEntity} containing the newly created {@link UserModel}.
     * @throws CreationException if an error occurs during the user creation process.
     */
    @PreAuthorize("hasAuthority('USER_CREATE')") @PostMapping("/create") @Override
    public @NotNull ResponseEntity<UserModel[]> create(@NotNull @RequestBody UserCreateModel[] model) throws CreationException
    {
        return super.create(model);
    }

    /**
     * Deletes a user identified by their unique id.
     * <p>
     * This method removes a {@link UserEntity} from the database based on the provided {@code id} and returns a {@code boolean} indicating
     * whether the deletion was successful.
     * <p>
     * Note that the invoking user must have the "<i>privilege.user.delete</i>" privilege,
     * which should be specified in the application's properties, to execute this action.
     *
     * @param id the unique identifier of the user to be deleted.
     * @return {@code true} if the user was successfully deleted; otherwise, {@code false}.
     */
    @PreAuthorize("hasAuthority('USER_DELETE')") @DeleteMapping("/delete/{id}")
    @Override public @NotNull ResponseEntity<Void> delete(@PathVariable @NotNull Long[] id)
    {
        return super.delete(id);
    }

    /**
     * Retrieves user data for the specified user id.
     * <p>
     * This method retrieves user data based on the provided user {@code id}.
     * <p>
     * The invoking user must have the "<i>privilege.user.get</i>" privilege, as configured in the application's properties,
     * or must be the owner of the data being requested to perform this action.
     *
     * @param id the unique identifier of the user whose data is being retrieved.
     * @return a {@link ResponseEntity} containing the requested {@link UserModel}.
     */
    @PreAuthorize("hasAuthority('USER_OTHERS_GET') or #id == authentication.principal")
    @GetMapping("/get/{id}") @Override public @NotNull ResponseEntity<UserModel> getData(@PathVariable @NotNull Long id)
    {
        return super.getData(id);
    }

    /**
     * Retrieves reduced user data for the specified user id.
     * <p>
     * This method retrieves reduced user data based on the provided user {@code id}.
     *
     * @param id the unique identifier of the user whose reduced data is being retrieved.
     * @return a {@link ResponseEntity} containing the requested {@link UserModel}.
     *
     * //TODO discuss privileges
     */
    @GetMapping("/get/{id}/reduced")
    public @NotNull ResponseEntity<ReducedUserModel> getReducedData(@PathVariable @NotNull Long id)
    {
        return getService().findReduced(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves data for the currently authenticated user.
     * <p>
     * This method retrieves user data for the authenticated user making the request. Access is restricted to
     * authenticated users who hold a valid {@link JwtTokenType#AUTHORIZED} token.
     * <p>
     * The invoking user must be the owner of the data being accessed to perform this action.
     *
     * @param user the currently authenticated user, provided automatically.
     * @return a {@link ResponseEntity} containing the requested {@link UserModel}.
     */
    @PreAuthorize("@verificationService.hasToken(T(de.gaz.eedu.user.verification.JwtTokenType).AUTHORIZED)")
    @GetMapping("/get") public @NotNull ResponseEntity<UserModel> getOwnData(@AuthenticationPrincipal long user)
    {
        return getData(user);
    }

    /**
     * Processes a login request for an anonymous user.
     * <p>
     * This method handles the login process for users who are not currently authenticated.
     * It accepts login credentials provided in the {@link UserLoginModel} and returns a
     * token if the authentication is successful.
     * <p>
     * Note that only anonymous users (i.e., users who are not authenticated) can access this endpoint.
     *
     * @param model an instance of {@link UserLoginModel} containing the user's login credentials.
     * @return a {@link ResponseEntity} containing a {@link String} token if the login is successful.
     */
    @PostMapping("/login")
    public @NotNull ResponseEntity<@Nullable String> requestNormalLogin(@NotNull @RequestBody UserLoginModel model)
    {
        log.info("The server has recognized an incoming normal login request login id {}.", model.loginName());
        return getService().requestLogin(model).map((token) ->
        {
            String jwt = token.jwt();
            return ResponseEntity.ok(jwt);
        }).orElseThrow(this::unauthorizedThrowable);
    }

    /**
     * Handles an advanced login request for an authenticated user.
     * <p>
     * This method processes advanced login requests, creating an instance of {@link AdvancedUserLoginModel}
     * using the provided {@code userID}. Upon successful processing of the login request, a response entity
     * containing a success message is returned. If the request cannot be authorized, an exception is thrown.
     * <p>
     * This endpoint is accessible only to authenticated users, identified by a valid authentication principal.
     *
     * @param userID the authenticated user, provided automatically
     *               through the {@link AuthenticationPrincipal} annotation.
     * @return a {@link ResponseEntity} containing a success message upon successful login.
     */
    @PostMapping("/login/advanced")
    public @NotNull ResponseEntity<String> requestAdvancedLogin(@AuthenticationPrincipal long userID)
    {
        log.info("The server has recognized an incoming advanced login request for {}.", userID);
        return getService().requestLogin(new AdvancedUserLoginModel(userID)).map((token) ->
        {
            String jwt = token.jwt();
            return ResponseEntity.ok(jwt);
        }).orElseThrow(this::unauthorizedThrowable);
    }

    /**
     * Logs out the current user and invalidates the authentication token.
     * <p>
     * This method handles the logout process by clearing the authentication cookie, effectively
     * invalidating the user's session. If the user is identified, a log entry is created; otherwise,
     * it is assumed that an anonymous user or a user with an expired token is being logged out.
     * <p>
     * This endpoint is accessible to all users, regardless of authentication status.
     *
     * @param user   the currently authenticated user, if available;
     *                 {@code null} if the user is not identified.
     * @param response the {@link HttpServletResponse} to which the logout cookie is added.
     */
    @GetMapping("/logout")
    public void logout(@AuthenticationPrincipal @Nullable Long user, @NotNull HttpServletResponse response)
    {
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setMaxAge(3600);
        cookie.setHttpOnly(true);
        cookie.setSecure(!development);
        response.addCookie(cookie);

        if (Objects.isNull(user))
        {
            log.info("An unidentified user has been logged out, likely due to token expiration.");
            return;
        }
        log.info("User {} has been logged out.", user);
    }

    @PreAuthorize("hasAnyAuthority('USER_OTHERS_GET')") @GetMapping("/all") @Override
    public @NotNull ResponseEntity<Set<UserModel>> fetchAll() { return super.fetchAll(); }

    @PreAuthorize("isAuthenticated()") @GetMapping("/all/reduced")
    public @NotNull ResponseEntity<ReducedUserModel[]> fetchAllReduced()
    {
        return ResponseEntity.ok(userService.findAllReduced().toArray(new ReducedUserModel[0]));
    }
}
