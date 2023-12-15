package de.gaz.eedu.livechat.message;

import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record MessageCreateModel(@NotNull Long authorId, @NotNull String body,
                                 @NotNull Long timeStamp, @NotNull MessageStatus status) implements CreationModel<MessageEntity>
{
    @Contract(pure = true) @Override
    public @NotNull String toString()
    {
        return "MessageCreateModel{" +
                "authorId=" + authorId +
                ", body='" + body + '\'' +
                ", timeStamp=" + timeStamp +
                ", status=" + status +
                '}';
    }

    @Contract(pure = true) @Override
    public @NotNull String name()
    {
        return System.currentTimeMillis() + " " + authorId();
    }

    @Override
    public @NotNull MessageEntity toEntity(@NotNull MessageEntity messageEntity)
    {
        messageEntity.setAuthorId(authorId());
        messageEntity.setBody(body());
        messageEntity.setTimestamp(System.currentTimeMillis());
        messageEntity.setStatus(status());
        return messageEntity;
    }
}
