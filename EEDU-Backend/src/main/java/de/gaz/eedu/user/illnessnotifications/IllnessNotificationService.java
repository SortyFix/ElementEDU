package de.gaz.eedu.user.illnessnotifications;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service public class IllnessNotificationService
{
    IllnessNotificationRepository illnessNotificationRepository;

    @NotNull public Optional<IllnessNotificationEntity> loadEntityById(@NotNull Long notificationId){
        return illnessNotificationRepository.findById(notificationId);
    }

    @NotNull public List<IllnessNotificationEntity> loadEntitiesByDate(@NotNull Long date){
        return illnessNotificationRepository.getIllnessNotificationEntitiesByNotificationDate(date);
    }

    @NotNull public List<IllnessNotificationEntity> loadEntitiesByUserIdWithStatus(@NotNull Long userId, @NotNull IllnessNotificationStatus status){
        return illnessNotificationRepository.getIllnessNotificationEntitiesByUserIdAndStatus(userId, status);
    }

    @NotNull public List<IllnessNotificationEntity> loadEntitiesByUserId(@NotNull Long userId){
        return illnessNotificationRepository.getIllnessNotificationEntitiesByUserId(userId);
    }

    @NotNull public IllnessNotificationEntity createEntity(@NotNull IllnessNotificationCreateModel illnessNotificationCreateModel){
        return illnessNotificationRepository.save(illnessNotificationCreateModel.toEntity(new IllnessNotificationEntity()));
    }
}
