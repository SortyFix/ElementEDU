package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public record IllnessNotificationCreateModel(@NotNull Long userId, @NotNull Long date, @NotNull String reason) implements CreationModel<IllnessNotificationEntity>
{
    @Override public String toString()
    {
        return "IllnessNotificationCreateModel{" +
                "userId=" + userId +
                ", date=" + date +
                '}';
    }

    // Please check this. I'm not sure here.
    @Override
    public @NotNull String name()
    {
        return reason;
    }

    @Override
    public @NotNull IllnessNotificationEntity toEntity(@NotNull IllnessNotificationEntity entity)
    {
        return IllnessNotificationEntity.builder()
                .notificationId(entity.getNotificationId())
                .userId(userId)
                .notificationDate(date)
                .status(IllnessNotificationStatus.PENDING)
                .reason(reason)
                .build();
    }
}
