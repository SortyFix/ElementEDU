package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.user.UserEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IllnessNotificationRepository extends JpaRepository<IllnessNotificationEntity, Long>
{
    @NotNull List<IllnessNotificationEntity> getIllnessNotificationEntitiesByUser(@NotNull UserEntity userId);
    @NotNull List<IllnessNotificationEntity> getIllnessNotificationEntitiesByTimeStamp(@NotNull Long date);
    @NotNull List<IllnessNotificationEntity> getIllnessNotificationEntitiesByStatus(@NotNull IllnessNotificationStatus status);
}
