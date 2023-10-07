package de.gaz.eedu.user;

import de.gaz.eedu.user.exception.UserEmailOccupiedException;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final UserService userService;

    /**
     * Request to create a {@link UserEntity}.
     * <p>
     * Allows incoming {@link RequestMethod#POST} request which contain a json body representing a {@link UserModel}.
     * This is then forwarded to the {@link UserService#createUser(UserModel)} method from the variable {@code userService} autowired using spring boots beans.
     * <p>
     * If a user with the same email as the {@link UserModel#email()} is present in the database, a {@link UserEmailOccupiedException} is thrown, and it returns null in the body.
     *
     * @param userModel the request requests to create.
     * @return the response either having the created userdata with the status {@link HttpStatus#CREATED} or a null body with a {@link HttpStatus#CONFLICT}.
     */
    @RequestMapping("/create") public @NotNull ResponseEntity<@Nullable UserModel> createUser(@NotNull @RequestBody UserModel userModel)
    {
        try {
            logger.info("The server has received an incoming request to create a user.");
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userModel));
        } catch (UserEmailOccupiedException userEmailOccupiedException) {
            logger.info("The email from the user create request was already taken.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }
}
