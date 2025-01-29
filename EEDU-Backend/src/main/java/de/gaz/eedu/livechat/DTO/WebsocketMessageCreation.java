package de.gaz.eedu.livechat.DTO;

import jakarta.validation.constraints.NotNull;

public record WebsocketMessageCreation(@NotNull Long authorId, @NotNull Long chatId, @NotNull String body) {
}
