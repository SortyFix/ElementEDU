package de.gaz.eedu.user;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.user.model.LoginModel;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.model.UserModel;
import de.gaz.eedu.user.verification.JwtTokenType;
import de.gaz.eedu.user.verification.model.AdvancedUserLoginModel;
import de.gaz.eedu.user.verification.model.UserLoginModel;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
import java.util.Optional;
import java.util.function.Function;

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
@Slf4j @RestController @RequestMapping(value = "/user") @RequiredArgsConstructor public class UserController extends EntityController<UserService, UserModel, UserCreateModel>
{
    private final UserService userService;
    @Value("${development}") private final boolean development = false;

    @Override protected @NotNull UserService getEntityService()
    {
        return userService;
    }

    @PreAuthorize("hasAuthority('${user.create}')") @PostMapping("/create") @Override
    public @NotNull ResponseEntity<UserModel> create(@NotNull @RequestBody UserCreateModel model)
    {
        return super.create(model);
    }

    @PreAuthorize("hasAuthority('${user.delete}')") @DeleteMapping("/delete/{id}") @Override
    public @NotNull Boolean delete(@PathVariable @NotNull Long id)
    {
        return super.delete(id);
    }

    @PreAuthorize("isAuthenticated() and (hasAuthority('ADMIN') or (#id == authentication.principal))")
    @GetMapping("/get/{id}") @Override public @NotNull ResponseEntity<UserModel> getData(@PathVariable @NotNull Long id)
    {
        validate(isAuthorized(JwtTokenType.AUTHORIZED), unauthorizedThrowable());
        return super.getData(id);
    }

    @PreAuthorize("isAuthenticated() and hasAuthority('AUTHORIZED')") @GetMapping("/get")
    public @NotNull ResponseEntity<UserModel> getOwnData(@AuthenticationPrincipal Long userId)
    {
        validate(isAuthorized(JwtTokenType.AUTHORIZED), unauthorizedThrowable());
        return super.getData(userId);
    }

    @PermitAll @PostMapping("/login")
    public @NotNull ResponseEntity<@Nullable String> requestNormalLogin(@NotNull @RequestBody UserLoginModel loginModel)
    {
        return requestLogin(loginModel).map(ResponseEntity::ok).orElseThrow(this::unauthorizedThrowable);
    }

    @PermitAll@GetMapping("/logout")
    public void logout(@AuthenticationPrincipal @Nullable Long userId, @NotNull HttpServletResponse response)
    {
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setMaxAge(3600);
        cookie.setHttpOnly(true);
        cookie.setSecure(!development);
        response.addCookie(cookie);

        if(Objects.isNull(userId))
        {
            log.info("An unidentified user has been logged out, likely due to token expiration.");
            return;
        }
        log.info("User {} has been logged out.", userId);
    }

    @PreAuthorize("isAuthenticated() and hasAuthority('AUTHORIZED')") @PostMapping("/login/advanced")
    public @NotNull ResponseEntity<String> requestAdvancedLogin(@NotNull @RequestBody AdvancedUserLoginModel loginModel,
                                                                @AuthenticationPrincipal long userID)
    {
        Function<UserEntity, Boolean> isAllowed = user -> user.getLoginName().equals(loginModel.loginName());

        validate(getEntityService().loadEntityById(userID).map(isAllowed).orElse(false), () ->
        {
            log.warn("A user tried to access the advanced token of another user. The request has been rejected.");
            return unauthorizedThrowable();
        });

        return requestLogin(loginModel).map(ResponseEntity::ok).orElseThrow(this::unauthorizedThrowable);
    }

    private @NotNull Optional<String> requestLogin(@NotNull LoginModel loginModel)
    {
        log.info("The server has recognized an incoming login request.");
        return getEntityService().requestLogin(loginModel);
    }
}
