package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.user.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController @RequestMapping(value = "/api/v1/illness/me") public class IllnessNotificationController
{
    private final UserService userService;
    private final IllnessNotificationService illnessNotificationService;

    // TODO: Only for parent accounts
    @PreAuthorize("isAuthenticated()") @PostMapping("/excuse") public ResponseEntity<Boolean> excuseCurrentUser(
            @AuthenticationPrincipal Long id, @NotNull String reason, @NotNull Long expirationTime, @NotNull MultipartFile file)
    {
        return userService.loadEntityById(id).map(userEntity ->
                ResponseEntity.ok(illnessNotificationService.excuse(id, reason, expirationTime, file)))
                          .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
