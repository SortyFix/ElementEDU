package de.gaz.eedu.livechat.message;

import de.gaz.eedu.entity.model.Model;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record MessageModel(@NotNull Long messageId, @NotNull Long authorId, @NotNull String body,
                           @NotNull Long timeStamp, MessageStatus status) implements Model
{
    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageModel that = (MessageModel) o;
        return Objects.equals(messageId, that.messageId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(messageId, author, body, timeStamp);
    }
}
