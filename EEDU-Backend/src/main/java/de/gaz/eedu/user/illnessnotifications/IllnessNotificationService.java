package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.file.FileService;
import de.gaz.eedu.user.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service public class IllnessNotificationService extends EntityService<IllnessNotificationRepository, IllnessNotificationEntity, IllnessNotificationModel, IllnessNotificationCreateModel>
{
    private final IllnessNotificationRepository illnessNotificationRepository;
    private final UserService userService;
    private final FileService fileService;

    @NotNull public List<IllnessNotificationEntity> loadEntitiesByDate(@NotNull Long date){
        return getRepository().getIllnessNotificationEntitiesByTimeStamp(date);
    }

    @NotNull public List<IllnessNotificationEntity> loadEntitiesByUserIdWithStatus(@NotNull Long userId, @NotNull IllnessNotificationStatus status){
        return getRepository().getIllnessNotificationEntitiesByUserAndStatus(userService.loadEntityByIDSafe(userId),
                status);
    }

    @NotNull public List<IllnessNotificationEntity> loadEntitiesByUserId(@NotNull Long userId){
        return getRepository().getIllnessNotificationEntitiesByUser(userService.loadEntityByIDSafe(userId));
    }

    @Override
    public @NotNull IllnessNotificationRepository getRepository()
    {
        return illnessNotificationRepository;
    }

    @Override
    public @NotNull IllnessNotificationEntity createEntity(@NotNull IllnessNotificationCreateModel model) throws CreationException
    {
        return illnessNotificationRepository.save(model.toEntity(new IllnessNotificationEntity(), (entity ->
        {
            entity.setUser(userService.loadEntityByIDSafe(model.userId()));
            entity.setFileEntity(fileService.getRepository().getReferenceById(model.fileId()));
            return entity;
        })));
        // same thing:
//        return illnessNotificationRepository.save(model.toEntity(new IllnessNotificationEntity(),
//                new CreationFactory<>() {
//                    @Override
//                    public @NotNull IllnessNotificationEntity transform(@NotNull IllnessNotificationEntity entity) {
//                        entity.setUser(userService.loadEntityByIDSafe(model.userId()));
//                        entity.setFileEntity(fileService.loadEntityByIDSafe(model.fileId()));
//                        return entity;
//                    }
//                }));
    }
}
