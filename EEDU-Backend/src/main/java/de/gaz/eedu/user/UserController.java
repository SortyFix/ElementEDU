package de.gaz.eedu.user;

import de.gaz.eedu.user.exception.LoginNameOccupiedException;
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

    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/create") public @NotNull ResponseEntity<@Nullable UserModel> createUser(@NotNull @RequestBody UserCreateModel userModel)
    {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(getUserService().create(userModel));
        } catch (LoginNameOccupiedException loginNameOccupiedException) {
            logger.info("The email from the user create request was already taken.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @PermitAll @GetMapping("/login") public @NotNull ResponseEntity<@Nullable String> loginUser(@NotNull @RequestBody UserLoginRequest userLoginRequest)
    {
        return getUserService().login(userLoginRequest).map(ResponseEntity::ok).orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
    }
}
