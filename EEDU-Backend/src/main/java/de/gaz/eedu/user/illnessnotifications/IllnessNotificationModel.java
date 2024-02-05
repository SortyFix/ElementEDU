package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.entity.model.EntityModel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record IllnessNotificationModel(@NotNull Long id, @NotNull Long userId, @NotNull IllnessNotificationStatus status, @NotNull String reason, @NotNull Long timestamp, @NotNull Long expirationTime) implements EntityModel
{
    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IllnessNotificationModel that = (IllnessNotificationModel) o;
        return Objects.equals(id, that.id) && Objects.equals(userId,
                that.userId) && status == that.status && Objects.equals(timestamp, that.timestamp) && Objects.equals(
                reason,
                that.reason);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id(), userId(), status(), timestamp(), reason());
    }
}
