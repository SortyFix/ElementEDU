package de.gaz.eedu.user.illnessnotifications.model;

import de.gaz.eedu.user.illnessnotifications.IllnessNotificationStatus;
import org.jetbrains.annotations.NotNull;

public record ReducedIllnessNotificationModel(@NotNull Long id, @NotNull Long userId, @NotNull IllnessNotificationStatus status,
                                              @NotNull Long timestamp, @NotNull Long expirationTime)
{
}
