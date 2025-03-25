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

    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).USER_CREATE.toString())") @PostMapping("/user/open")
    public ResponseEntity<IllnessNotificationModel[]> getNotificationsWithStatusOfUser(@NotNull @RequestBody Long userId, @NotNull @RequestBody IllnessNotificationStatus status){
        return illnessNotificationManagementService.getNotificationsOfUserByStatus(userId, status);
    }

    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).USER_CREATE.toString())") @PostMapping("/user/all")
    public ResponseEntity<IllnessNotificationModel[]> getNotificationsOfUser(@NotNull @RequestBody Long userId){
        return illnessNotificationManagementService.getNotificationsOfUser(userId);
    }

    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).USER_CREATE.toString())") @GetMapping("/{date}")
    public ResponseEntity<IllnessNotificationModel[]> getNotificationsOfDate(@NotNull @PathVariable Long date){
        return illnessNotificationManagementService.getNotificationsOfDate(date);
    }

    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).USER_CREATE.toString())")
    @GetMapping("/get-pending")
    public ResponseEntity<List<IllnessNotificationModel>> getPendingNotifications()
    {
        return illnessNotificationManagementService.getPendingNotfications();
    }

    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).USER_CREATE.toString())")
    @PutMapping("/respond/{notificationId}")
    public ResponseEntity<Boolean> respondToNotification(@NotNull @PathVariable Long notificationId, @NotNull
    @RequestBody IllnessNotificationStatus status)
    {
        return illnessNotificationManagementService.respondToNotification(notificationId, status);
    }
}
