package de.gaz.eedu.management.illnessnotifications;

import de.gaz.eedu.user.illnessnotifications.model.IllnessNotificationModel;
import de.gaz.eedu.user.illnessnotifications.IllnessNotificationStatus;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping(value = "/api/v1/illness/management") @AllArgsConstructor
public class IllnessNotificationManagementController
{
    private final IllnessNotificationManagementService illnessNotificationManagementService;

    @PreAuthorize("hasAuthority('ADMINISTRATOR')") @PostMapping("/user/open")
    public ResponseEntity<IllnessNotificationModel[]> getNotificationsWithStatusOfUser(@NotNull @RequestBody Long userId, @NotNull @RequestBody IllnessNotificationStatus status){
        return illnessNotificationManagementService.getNotificationsOfUserByStatus(userId, status);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')") @PostMapping("/user/all")
    public ResponseEntity<IllnessNotificationModel[]> getNotificationsOfUser(@NotNull @RequestBody Long userId){
        return illnessNotificationManagementService.getNotificationsOfUser(userId);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')") @GetMapping("/{date}")
    public ResponseEntity<IllnessNotificationModel[]> getNotificationsOfDate(@NotNull @PathVariable Long date){
        return illnessNotificationManagementService.getNotificationsOfDate(date);
    }

    @GetMapping("/get-pending")
    public ResponseEntity<List<IllnessNotificationModel>> getPendingNotifications()
    {
        return illnessNotificationManagementService.getPendingNotfications();
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')") @PostMapping("/respond/{notificationId}/{status}")
    public ResponseEntity<Boolean> respondToNotification(@NotNull @PathVariable Long notificationId, @NotNull
    @PathVariable IllnessNotificationStatus status)
    {
        return illnessNotificationManagementService.respondToNotification(notificationId, status);
    }
}
