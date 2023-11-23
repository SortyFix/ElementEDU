package de.gaz.eedu.user.illness_notifications;

import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.UserStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController @RequestMapping(value = "/illness")
public class IllnessNotificationController
{
    private final IllnessNotificationService illnessNotificationService;
    private final UserService userService;
    private String currentUsername;

    public IllnessNotificationController(IllnessNotificationService illnessNotificationService, UserService userService) {
        this.illnessNotificationService = illnessNotificationService;
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()") @PostMapping("/me/excuse") public ResponseEntity<Boolean> excuseCurrentUser(@AuthenticationPrincipal Long id)
    {
        return userService.loadEntityByID(id).map(userEntity -> {
            userEntity.setStatus(UserStatus.EXCUSED);
            return ResponseEntity.ok(true);
        }).orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false));
    }

}
