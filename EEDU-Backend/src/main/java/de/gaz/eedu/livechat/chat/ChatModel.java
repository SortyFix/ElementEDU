package de.gaz.eedu.livechat.chat;

import de.gaz.eedu.entity.model.Model;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public record ChatModel(@NotNull Long chatId, @NotNull Long timeOfCreation, @NotNull Long[] users, @NotNull Long[] chatHistory) implements Model
{
    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatModel chatModel = (ChatModel) o;
        return Objects.equals(chatId, chatModel.chatId);
    }

    @Override
    public int hashCode()
    {
        int result = Objects.hash(chatId);
        result = 31 * result + Arrays.hashCode(users);
        result = 31 * result + Arrays.hashCode(chatHistory);
        return result;
    }
}
