package de.gaz.eedu.management.illnessnotifications;

import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.UserStatus;
import de.gaz.eedu.user.illnessnotifications.IllnessNotificationEntity;
import de.gaz.eedu.user.illnessnotifications.IllnessNotificationModel;
import de.gaz.eedu.user.illnessnotifications.IllnessNotificationService;
import de.gaz.eedu.user.illnessnotifications.IllnessNotificationStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController @RequestMapping(value = "/illness/management")
public class IllnessNotificationManagementController
{
    private final IllnessNotificationService illnessNotificationService;
    private final UserService userService;

    public IllnessNotificationManagementController(@NotNull IllnessNotificationService illnessNotificationService,
            @NotNull UserService userService)
    {
        this.illnessNotificationService = illnessNotificationService;
        this.userService = userService;
    }

    // As already stated, authority still open for discussion

    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/user/open")
    public ResponseEntity<List<IllnessNotificationModel>> getNotificationsWithStatusOfUser(@NotNull Long userId, @NotNull IllnessNotificationStatus status){
        return ResponseEntity.ok(illnessNotificationService.loadEntitiesByUserIdWithStatus(userId, status).stream().map(
                IllnessNotificationEntity::toModel).collect(Collectors.toList()));
    }

    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/user/all")
    public ResponseEntity<List<IllnessNotificationModel>> getNotificationsOfUser(@NotNull Long userId){
        return ResponseEntity.ok(illnessNotificationService.loadEntitiesByUserId(userId).stream().map(
                IllnessNotificationEntity::toModel).collect(Collectors.toList()));
    }

    @PreAuthorize("hasAuthority('ADMIN')") @GetMapping("/{date}")
    public ResponseEntity<List<IllnessNotificationModel>> getNotificationsOfDate(@NotNull @PathVariable Long date){
        return ResponseEntity.ok(illnessNotificationService.loadEntitiesByDate(date).stream().map(IllnessNotificationEntity::toModel).collect(Collectors.toList()));
    }

    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/respond")
    public ResponseEntity<Boolean> respondToNotification(@NotNull Long notificationId, @NotNull IllnessNotificationStatus status)
    {
        return illnessNotificationService.loadEntityById(notificationId).map(illnessNotificationEntity -> {
            illnessNotificationEntity.setStatus(status);
            userService.loadEntityByID(illnessNotificationEntity.getUserId()).ifPresentOrElse(userEntity ->
                            userEntity.setStatus(status == IllnessNotificationStatus.ACCEPTED ? UserStatus.EXCUSED : UserStatus.UNEXCUSED),
                    () -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(false));
            return ResponseEntity.ok(true);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }
}
