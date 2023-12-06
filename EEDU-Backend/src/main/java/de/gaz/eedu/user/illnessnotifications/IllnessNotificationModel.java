package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.entity.model.Model;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record IllnessNotificationModel(@NotNull Long illnessId, @NotNull Long userId, @NotNull IllnessNotificationStatus status, @NotNull java.time.LocalDate date, @NotNull String reason) implements Model
{
    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IllnessNotificationModel that = (IllnessNotificationModel) o;
        return Objects.equals(illnessId, that.illnessId) && Objects.equals(userId,
                that.userId) && status == that.status && Objects.equals(date, that.date) && Objects.equals(reason,
                that.reason);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(illnessId, userId, status, date, reason);
    }
}
