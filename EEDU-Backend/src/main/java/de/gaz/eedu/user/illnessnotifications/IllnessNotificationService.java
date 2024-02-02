package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service public class IllnessNotificationService implements EntityService<IllnessNotificationRepository, IllnessNotificationEntity, IllnessNotificationModel, IllnessNotificationCreateModel>
{
    private final IllnessNotificationRepository illnessNotificationRepository;
    private final UserRepository userRepository;

    @NotNull public List<IllnessNotificationEntity> loadEntitiesByDate(@NotNull Long date){
        return getRepository().getIllnessNotificationEntitiesByTimeStamp(date);
    }

    @NotNull public List<IllnessNotificationEntity> loadEntitiesByUserIdWithStatus(@NotNull Long userId, @NotNull IllnessNotificationStatus status){
        return getRepository().getIllnessNotificationEntitiesByUserAndStatus(userRepository.getReferenceById(userId), status);
    }

    @NotNull public List<IllnessNotificationEntity> loadEntitiesByUserId(@NotNull Long userId){
        return getRepository().getIllnessNotificationEntitiesByUser(userRepository.getReferenceById(userId));
    }

    @Override
    public @NotNull IllnessNotificationRepository getRepository()
    {
        return illnessNotificationRepository;
    }

    @NotNull public IllnessNotificationEntity createEntity(@NotNull IllnessNotificationCreateModel illnessNotificationCreateModel){
        return illnessNotificationRepository.save(illnessNotificationCreateModel.toINEntity(userRepository.getReferenceById(illnessNotificationCreateModel.userId())));
    }
}
