package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.file.FileCreateModel;
import de.gaz.eedu.file.FileEntity;
import de.gaz.eedu.file.FileService;
import de.gaz.eedu.file.exception.MaliciousFileException;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.UserStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service public class IllnessNotificationService extends EntityService<IllnessNotificationRepository, IllnessNotificationEntity, IllnessNotificationModel, IllnessNotificationCreateModel>
{
    private final IllnessNotificationRepository illnessNotificationRepository;
    private final UserService userService;
    private final FileService fileService;

    @NotNull public List<IllnessNotificationEntity> loadEntitiesByDate(@NotNull Long date){
        return getRepository().getIllnessNotificationEntitiesByTimeStamp(date);
    }

    @Override
    public @NotNull IllnessNotificationRepository getRepository()
    {
        return illnessNotificationRepository;
    }

    @Transactional
    public boolean excuse(@NotNull Long userId, @NotNull String reason, @NotNull Long expirationTime, @Nullable MultipartFile file)
    {
        @Nullable Long fileId = uploadNotification(file);
        createEntity(Set.of(new IllnessNotificationCreateModel(userId,
                reason,
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond(),
                expirationTime,
                fileId)));

        return true;
    }

    public @Nullable Long uploadNotification(@Nullable MultipartFile file){
        if(!(file != null && !file.isEmpty()))
        {
            return null;
        }

        // TODO: Add logic if current day is an exam day
        FileEntity fileEntity = fileService.createEntity(new FileCreateModel(
                "illness_notifications",
                new String[] { "Management" },
                new String[] { "illness_notification" }));

        try
        {
            fileEntity.uploadBatch("", file);
            return fileEntity.getId();
        }
        catch (MaliciousFileException e)
        {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<Boolean> respondToNotification(@NotNull IllnessNotificationEntity entity, @NotNull IllnessNotificationStatus status)
    {
        entity.setStatus(status);
        userService.loadEntityById(entity.getUser().getId()).ifPresentOrElse(userEntity ->
                        userEntity.setStatus(status.equals(IllnessNotificationStatus.ACCEPTED) ? UserStatus.EXCUSED : UserStatus.UNEXCUSED),
                () -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(false));
        return ResponseEntity.ok(true);
    }

    @Override
    public @NotNull List<IllnessNotificationEntity> createEntity(@NotNull Set<IllnessNotificationCreateModel> model) throws CreationException
    {
        List<IllnessNotificationEntity> entities = model.stream().map(createModel ->
                createModel.toEntity(new IllnessNotificationEntity(), (entity ->
                {
                    entity.setUser(userService.loadEntityByIDSafe(createModel.userId()));
                    entity.setFileEntity(fileService.getRepository().getReferenceById(createModel.fileId()));
                    return entity;
                }))).toList();
        return illnessNotificationRepository.saveAll(entities);
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
