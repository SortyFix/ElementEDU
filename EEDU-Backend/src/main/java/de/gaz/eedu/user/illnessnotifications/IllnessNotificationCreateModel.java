package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public record IllnessNotificationCreateModel(@NotNull Long userId, @NotNull String reason, @NotNull Long timestamp,
                                             @NotNull Long expirationTime,
                                             @NotNull Long fileId) implements CreationModel<IllnessNotificationEntity>
{
    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IllnessNotificationCreateModel that = (IllnessNotificationCreateModel) o;
        return Objects.equals(userId, that.userId) && Objects.equals(timestamp,
                that.timestamp) && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(userId, timestamp, reason);
    }

    @Override
    public @NotNull IllnessNotificationEntity toEntity(@NotNull IllnessNotificationEntity entity)
    {
        entity.setStatus(IllnessNotificationStatus.PENDING);
        entity.setReason(reason());
        entity.setTimeStamp(timestamp());
        entity.setExpirationTime(expirationTime());
        return entity;
    }
}
