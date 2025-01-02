package de.gaz.eedu.livechat.DTO;

import jakarta.validation.constraints.NotNull;

public record WebsocketChatEdit(@NotNull Long userId, @NotNull Long chatId)
{
}
