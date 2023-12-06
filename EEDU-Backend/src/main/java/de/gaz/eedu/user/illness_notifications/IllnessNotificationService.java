package de.gaz.eedu.user.illness_notifications;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service @AllArgsConstructor @Getter(AccessLevel.PROTECTED) public class IllnessNotificationService
{
    private final IllnessNotificationRepository illnessNotificationRepository;

    @NotNull public Optional<IllnessNotificationEntity> loadEntityById(@NotNull Long notificationId){
        return getIllnessNotificationRepository().findById(notificationId);
    }

    @NotNull public List<IllnessNotificationEntity> loadEntitiesByDate(@NotNull LocalDate date){
        return getIllnessNotificationRepository().getIllnessNotificationEntityByNotificationDate(date);
    }

    @NotNull public List<IllnessNotificationEntity> loadEntitiesByUserIdWithStatus(@NotNull Long userId, @NotNull IllnessNotificationStatus status){
        return getIllnessNotificationRepository().getIllnessNotificationEntityByUserIdAndStatus(userId, status);
    }
}
