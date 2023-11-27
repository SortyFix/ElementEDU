package de.gaz.eedu.user.illness_notifications;

import de.gaz.eedu.entity.model.Model;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record IllnessNotificationModel(@NotNull Long illnessId, @NotNull Long userId, @NotNull IllnessNotificationStatus status, java.time.LocalDate date) implements Model
{
    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IllnessNotificationModel that = (IllnessNotificationModel) o;
        return Objects.equals(illnessId, that.illnessId) && Objects.equals(userId, that.userId) && status == that.status && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(illnessId, userId, status, date);
    }
}
