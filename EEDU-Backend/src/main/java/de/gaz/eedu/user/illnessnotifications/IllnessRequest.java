package de.gaz.eedu.user.illnessnotifications;

import org.jetbrains.annotations.NotNull;

public record IllnessRequest(@NotNull String reason, @NotNull Long expirationTime)
{
}
