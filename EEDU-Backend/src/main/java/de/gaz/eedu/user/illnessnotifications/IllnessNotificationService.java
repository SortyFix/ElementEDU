package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.user.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service public class IllnessNotificationService
{
    IllnessNotificationRepository illnessNotificationRepository;
    UserRepository userRepository;

    @NotNull public Optional<IllnessNotificationEntity> loadEntityById(@NotNull Long notificationId){
        return illnessNotificationRepository.findById(notificationId);
    }

    @NotNull public Boolean delete(Long id)
    {
        return illnessNotificationRepository.findById(id).map(entity ->{
            illnessNotificationRepository.deleteById(id);
            return true;
        }).orElse(false);
    }

    @NotNull public List<IllnessNotificationEntity> loadEntitiesByDate(@NotNull Long date){
        return illnessNotificationRepository.getIllnessNotificationEntitiesByTimeStamp(date);
    }

    @NotNull public List<IllnessNotificationEntity> loadEntitiesByUserIdWithStatus(@NotNull Long userId, @NotNull IllnessNotificationStatus status){
        return illnessNotificationRepository.getIllnessNotificationEntitiesByUserAndStatus(userRepository.getReferenceById(userId), status);
    }

    @NotNull public List<IllnessNotificationEntity> loadEntitiesByUserId(@NotNull Long userId){
        return illnessNotificationRepository.getIllnessNotificationEntitiesByUser(userRepository.getReferenceById(userId));
    }

    @NotNull public IllnessNotificationEntity createEntity(@NotNull IllnessNotificationCreateModel illnessNotificationCreateModel){
        return illnessNotificationRepository.save(illnessNotificationCreateModel.toINEntity(userRepository.getReferenceById(illnessNotificationCreateModel.userId()), new IllnessNotificationEntity()));
    }
}
