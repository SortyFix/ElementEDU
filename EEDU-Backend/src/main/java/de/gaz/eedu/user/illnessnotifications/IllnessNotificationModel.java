package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.entity.model.Model;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record IllnessNotificationModel(@NotNull Long notificationId, @NotNull Long userId, @NotNull IllnessNotificationStatus status, Long timestamp, @NotNull String reason) implements Model
{
    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IllnessNotificationModel that = (IllnessNotificationModel) o;
        return Objects.equals(notificationId, that.notificationId) && Objects.equals(userId,
                that.userId) && status == that.status && Objects.equals(timestamp, that.timestamp) && Objects.equals(
                reason,
                that.reason);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(notificationId(), userId(), status(), timestamp(), reason());
    }
}
