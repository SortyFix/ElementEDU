package de.gaz.eedu.management.illnessnotifications;

import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.illnessnotifications.IllnessNotificationEntity;
import de.gaz.eedu.user.illnessnotifications.model.IllnessNotificationModel;
import de.gaz.eedu.user.illnessnotifications.IllnessNotificationService;
import de.gaz.eedu.user.illnessnotifications.IllnessNotificationStatus;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service @AllArgsConstructor
public class IllnessNotificationManagementService
{
    private final IllnessNotificationService illnessNotificationService;
    private final UserService userService;

    public ResponseEntity<IllnessNotificationModel[]> getNotificationsOfUserByStatus(@NotNull Long userId, @NotNull IllnessNotificationStatus status)
    {
        return ResponseEntity.ok(userService.loadEntityById(userId).orElseThrow(() -> new ResponseStatusException(
                                                    HttpStatus.NOT_FOUND))
                                            .getIllnessNotificationEntities()
                                            .stream()
                                            .filter(illnessNotificationEntity -> illnessNotificationEntity.getStatus().equals(status))
                                            .map(IllnessNotificationEntity::toModel)
                                            .toArray(IllnessNotificationModel[]::new));
    }

    public ResponseEntity<IllnessNotificationModel[]> getNotificationsOfUser(@NotNull Long userId){
        return ResponseEntity.ok(userService.loadEntityById(userId)
                                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                                            .getIllnessNotificationEntities()
                                            .stream()
                                            .map(IllnessNotificationEntity::toModel)
                                            .toArray(IllnessNotificationModel[]::new));
    }

    public ResponseEntity<IllnessNotificationModel[]> getNotificationsOfDate(@NotNull Long date){
        return ResponseEntity.ok(illnessNotificationService.loadEntitiesByDate(date)
                                                           .stream()
                                                           .map(IllnessNotificationEntity::toModel)
                                                           .toArray(IllnessNotificationModel[]::new));
    }

    public ResponseEntity<List<IllnessNotificationModel>> getPendingNotfications()
    {
        return illnessNotificationService.getPendingNotifications();
    }

    public ResponseEntity<Boolean> respondToNotification(@NotNull @PathVariable Long notificationId, @NotNull IllnessNotificationStatus status)
    {
        return illnessNotificationService.loadEntityById(notificationId)
                                         .map(illnessNotificationEntity ->
                                                 illnessNotificationService.respondToNotification(illnessNotificationEntity, status))
                                         .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }
}
