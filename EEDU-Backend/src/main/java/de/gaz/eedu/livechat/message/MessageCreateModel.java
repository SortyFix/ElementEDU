package de.gaz.eedu.livechat.message;

import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.user.UserEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record MessageCreateModel(@NotNull Long authorId, @NotNull String body, @NotNull Long timestamp, @NotNull MessageStatus status) implements CreationModel<MessageEntity>
{
    @Contract(pure = true) @Override public @NotNull String toString()
    {
        return "MessageCreateModel{" +
                "author=" + authorId() +
                ", body='" + body + '\'' +
                '}';
    }

    @Override
    public @NotNull String name()
    {
        return System.currentTimeMillis() + " " + authorId();
    }

    @Override
    public @NotNull MessageEntity toEntity(@NotNull MessageEntity messageEntity)
    {
        return messageEntity;
    }

    // Necessary for future tests, don't remove
    public @NotNull MessageEntity toMessageEntity(@NotNull UserEntity author, @NotNull MessageEntity messageEntity){
        messageEntity.setAuthor(author);
        messageEntity.setBody(body());
        messageEntity.setTimestamp(timestamp());
        messageEntity.setStatus(MessageStatus.UNREAD);
        return messageEntity;
    }
}
