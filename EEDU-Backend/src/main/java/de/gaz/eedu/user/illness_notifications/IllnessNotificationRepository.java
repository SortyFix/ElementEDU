package de.gaz.eedu.user.illness_notifications;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface IllnessNotificationRepository extends JpaRepository<IllnessNotificationEntity, Long>
{
    @NotNull List<IllnessNotificationEntity> getIllnessNotificationEntitiesByUserId(@NotNull Long userId);
    @NotNull List<IllnessNotificationEntity> getIllnessNotificationEntityByNotificationDate(@NotNull LocalDate date);
    @NotNull List<IllnessNotificationEntity> getIllnessNotificationEntityByUserIdAndStatus(@NotNull Long userId, @NotNull IllnessNotificationStatus status);
}

