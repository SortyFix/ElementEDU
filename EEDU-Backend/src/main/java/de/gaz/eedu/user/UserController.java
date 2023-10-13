package de.gaz.eedu.user;

import de.gaz.eedu.user.exception.InsecurePasswordException;
import de.gaz.eedu.user.exception.LoginNameOccupiedException;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.model.UserLoginModel;
import de.gaz.eedu.user.model.UserLoginVerificationModel;
import de.gaz.eedu.user.model.UserModel;
import jakarta.annotation.security.PermitAll;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
@RestController
@RequestMapping(value = "/user", method = RequestMethod.POST)
@AllArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Getter(AccessLevel.PROTECTED)
    private final UserService userService;

    /**
     * Creates a user by a http request.
     * <p>
     * This method creates and is triggered by http requests from the frontend.
     * Note that this method requires an authorization, which is managed by the {@link de.gaz.eedu.security.JwtAuthorizationFilter}
     * and the {@link PreAuthorize} annotation.
     * <p>
     * The following status codes can be returned:
     * <p>
     *     - 201 (Created) <i>When the user was successfully created. A {@link UserModel} will be inside the body.</i>
     * </p>
     * <p>
     *     - 409 (Conflict) <i>When the login name is already taken. Body is null.</i>
     * </p>
     * <p>
     *     - 406 (Not Acceptable) <i>When the password does not matches the requirements. Body is null.</i>
     * </p>
     * <p>
     * This method is a post request as it creates a {@link UserEntity} and turns it into a {@link UserModel} which it then returns.
     *
     * @param userCreateModel the model of the user that should be created.
     * @return a response entity containing the status code and the required body.
     */
    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/create") public @NotNull ResponseEntity<@Nullable UserModel> createUser(@NotNull @RequestBody UserCreateModel userCreateModel)
    {
        HttpStatus httpStatus;
        logger.info("The server has recognized an incoming user create request.");
        try
        {
            return ResponseEntity.status(HttpStatus.CREATED).body(getUserService().create(userCreateModel));
        }
        catch (LoginNameOccupiedException loginNameOccupiedException)
        {
            logger.info("The email from the previously mentioned user create request was already taken.");
            httpStatus = HttpStatus.CONFLICT;
        }
        catch (InsecurePasswordException insecurePasswordException)
        {
            logger.info("The from the previously mentioned user create request password did not match the requirements.");
            httpStatus = HttpStatus.NOT_ACCEPTABLE;
        }
        return ResponseEntity.status(httpStatus).body(null);
    }

    @PreAuthorize("isAuthenticated()") @GetMapping("/me/{id}") public @NotNull ResponseEntity<@Nullable UserModel> getUserData(@NotNull @PathVariable Long id)
    {
        logger.info("The server has recognized a incoming self receiving data request for user.");
        return getUserService().loadById(id).map(ResponseEntity::ok).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PermitAll @PostMapping("/login") public @NotNull ResponseEntity<@Nullable UserLoginVerificationModel> loginUser(@NotNull @RequestBody UserLoginModel userLoginModel)
    {
        logger.info("The server has recognized a incoming login request.");
        return getUserService().login(userLoginModel).map(ResponseEntity::ok).orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
    }
}
