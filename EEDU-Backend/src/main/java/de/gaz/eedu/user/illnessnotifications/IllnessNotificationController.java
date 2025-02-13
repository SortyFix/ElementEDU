package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.illnessnotifications.model.ReducedIllnessNotificationModel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@RestController @RequestMapping(value = "/api/v1/illness/me") public class IllnessNotificationController
{
    private final UserService userService;
    private final IllnessNotificationService illnessNotificationService;

    // TODO: Only for parent accounts
    @PreAuthorize("isAuthenticated()") @PostMapping("/excuse") public ResponseEntity<Boolean> excuseCurrentUser(
            @AuthenticationPrincipal Long id, @RequestParam("reason") @NotNull String reason,
            @RequestParam("expirationTime") @NotNull Long expirationTime, @RequestParam(value = "file") MultipartFile file)
    {
        return userService.loadEntityById(id).map(userEntity ->
                        ResponseEntity.ok(illnessNotificationService.excuse(id, reason, expirationTime, file)))
                                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    @PreAuthorize("isAuthenticated()") @GetMapping("/my-notifications")
    public ResponseEntity<List<ReducedIllnessNotificationModel>> getOwnNotifications(@AuthenticationPrincipal Long id)
    {
        return illnessNotificationService.getReducedEntitiesByUser(id);
    }
}
