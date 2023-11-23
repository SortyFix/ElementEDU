package de.gaz.eedu.user.illness_notifications;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IllnessNotificationRepository extends JpaRepository<IllnessNotificationEntity, Long>
{
    @NotNull List<IllnessNotificationEntity> getIllnessNotificationEntitiesByUserId(@NotNull Long userId);
}
