package de.gaz.eedu.user.illnessnotifications;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IllnessNotificationRepository extends JpaRepository<IllnessNotificationEntity, Long>
{
    @NotNull List<IllnessNotificationEntity> getIllnessNotificationEntitiesByUserId(@NotNull Long userId);
    @NotNull List<IllnessNotificationEntity> getIllnessNotificationEntitiesByNotificationDate(@NotNull Long date);
    @NotNull List<IllnessNotificationEntity> getIllnessNotificationEntitiesByUserIdAndStatus(@NotNull Long userId, @NotNull IllnessNotificationStatus status);
}
