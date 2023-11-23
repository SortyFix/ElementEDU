package de.gaz.eedu.user.illness_notifications;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service public class IllnessNotificationService
{
    IllnessNotificationRepository illnessNotificationRepository;

    @NotNull public Optional<IllnessNotificationEntity> loadEntityById(@NotNull Long notificationId){
        return illnessNotificationRepository.findById(notificationId);
    }
}
