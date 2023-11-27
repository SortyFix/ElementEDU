package de.gaz.eedu.user.illness_notifications;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service public class IllnessNotificationService
{
    IllnessNotificationRepository illnessNotificationRepository;

    @NotNull public Optional<IllnessNotificationEntity> loadEntityById(@NotNull Long notificationId){
        return illnessNotificationRepository.findById(notificationId);
    }

    @NotNull public List<IllnessNotificationEntity> loadEntitiesByDate(@NotNull LocalDate date){
        return illnessNotificationRepository.getIllnessNotificationEntityByNotificationDate(date);
    }

    @NotNull public List<IllnessNotificationEntity> loadEntitiesByUserIdWithStatus(@NotNull Long userId, @NotNull IllnessNotificationStatus status){
        return illnessNotificationRepository.getIllnessNotificationEntityByUserIdAndStatus(userId, status);
    }
}
