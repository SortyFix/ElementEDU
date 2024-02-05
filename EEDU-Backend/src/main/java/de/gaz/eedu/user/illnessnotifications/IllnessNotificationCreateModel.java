package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.user.UserEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public record IllnessNotificationCreateModel(@NotNull Long userId, @NotNull String reason, @NotNull Long timestamp, @NotNull Long expirationTime) implements CreationModel<IllnessNotificationEntity>
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

    // Please check this. I'm not sure here.
    @Override
    public @NotNull String name()
    {
        return reason + " " + timestamp();
    }

    @Override
    public @NotNull IllnessNotificationEntity toEntity(@NotNull IllnessNotificationEntity entity)
    {
        return entity;
    }

    public @NotNull IllnessNotificationEntity toINEntity(@NotNull UserEntity userEntity){
        IllnessNotificationEntity newEntity = new IllnessNotificationEntity();
        newEntity.setUser(userEntity);
        newEntity.setStatus(IllnessNotificationStatus.PENDING);
        newEntity.setReason(reason());
        newEntity.setTimeStamp(timestamp());
        newEntity.setExpirationTime(expirationTime());
        return newEntity;
    }
}
