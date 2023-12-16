package de.gaz.eedu.livechat.message;

import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;

public record MessageCreateModel(@NotNull Long authorId, @NotNull String body) implements CreationModel<MessageEntity>
{
    @Override public String toString()
    {
        return "MessageCreateModel{" +
                "authorId=" + authorId +
                ", body='" + body + '\'' +
                '}';
    }


    @Override
    public @NotNull String name()
    {
        return System.currentTimeMillis() + " " + authorId;
    }

    @Override
    public @NotNull MessageEntity toEntity(@NotNull MessageEntity messageEntity)
    {
        messageEntity.setAuthorId(authorId());
        messageEntity.setBody(body());
        messageEntity.setTimestamp(System.currentTimeMillis());
        messageEntity.setStatus(MessageStatus.UNREAD);
        return messageEntity;
    }
}
