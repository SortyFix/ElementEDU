package de.gaz.eedu.management.illnessnotifications;

import de.gaz.eedu.user.illnessnotifications.IllnessNotificationModel;
import de.gaz.eedu.user.illnessnotifications.IllnessNotificationStatus;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping(value = "/illness/management") @AllArgsConstructor
public class IllnessNotificationManagementController
{
    private final IllnessNotificationManagementService illnessNotificationManagementService;

    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/user/open")
    public ResponseEntity<IllnessNotificationModel[]> getNotificationsWithStatusOfUser(@NotNull Long userId, @NotNull IllnessNotificationStatus status){
        return illnessNotificationManagementService.getNotificationsOfUserByStatus(userId, status);
    }

    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/user/all")
    public ResponseEntity<IllnessNotificationModel[]> getNotificationsOfUser(@NotNull Long userId){
        return illnessNotificationManagementService.getNotificationsOfUser(userId);
    }

    @PreAuthorize("hasAuthority('ADMIN')") @GetMapping("/{date}")
    public ResponseEntity<IllnessNotificationModel[]> getNotificationsOfDate(@NotNull @PathVariable Long date){
        return illnessNotificationManagementService.getNotificationsOfDate(date);
    }

    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/respond/{notificationId}/{status}")
    public ResponseEntity<Boolean> respondToNotification(@NotNull @PathVariable Long notificationId, @NotNull
    @PathVariable IllnessNotificationStatus status)
    {
        return illnessNotificationManagementService.respondToNotification(notificationId, status);
    }
}
