package de.gaz.eedu.user.illnessnotifications;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record IllnessRequest(@NotNull String reason, @NotNull Long expirationTime)
{
}
