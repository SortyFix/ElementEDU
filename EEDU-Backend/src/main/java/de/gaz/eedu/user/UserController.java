package de.gaz.eedu.user;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.model.UserLoginModel;
import de.gaz.eedu.user.model.UserLoginVerificationModel;
import de.gaz.eedu.user.model.UserModel;
import jakarta.annotation.security.PermitAll;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
@RestController @RequestMapping(value = "/user") public class UserController extends EntityController<UserService,
        UserModel, UserCreateModel>
{

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(@Autowired UserService entityService)
    {
        super(entityService);
    }

    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/create") @Override public @NotNull ResponseEntity<UserModel> create(@NotNull @RequestBody UserCreateModel model)
    {
        return super.create(model);
    }

    @PreAuthorize("isAuthenticated()") @DeleteMapping("/delete/{id}") @Override public @NotNull Boolean delete(@PathVariable @NotNull Long id)
    {
        return super.delete(id);
    }

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/{id}") @Override public @NotNull ResponseEntity<UserModel> getData(@PathVariable @NotNull Long id)
    {
        return super.getData(id);
    }

    @PermitAll @PostMapping("/login") public @NotNull ResponseEntity<@Nullable UserLoginVerificationModel> loginUser(@NotNull @RequestBody UserLoginModel userLoginModel)
    {
        logger.info("The server has recognized an incoming login request.");
        return getEntityService().login(userLoginModel).map(ResponseEntity::ok).orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
    }
}
