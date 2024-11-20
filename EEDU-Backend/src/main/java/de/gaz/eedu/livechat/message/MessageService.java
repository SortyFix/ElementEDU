package de.gaz.eedu.livechat.message;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.user.UserRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Service @RequiredArgsConstructor @Getter(AccessLevel.PROTECTED) public class MessageService extends EntityService<MessageRepository, MessageEntity, MessageModel, MessageCreateModel>
{
    @Getter(AccessLevel.NONE)
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageEntity findEntityById(@NotNull Long id){
        return getRepository().findMessageEntityByMessageId(id);
    }

    @Override
    public @NotNull MessageRepository getRepository()
    {
        return messageRepository;
    }

    public @NotNull List<MessageEntity> createEntity(@NotNull Set<MessageCreateModel> messageCreateModel)
    {
        List<MessageEntity> messageEntities = messageCreateModel.stream().map(model ->
                model.toMessageEntity(getUserRepository().getReferenceById(model.authorId()), new MessageEntity())).toList();
        return getRepository().saveAll(messageEntities);
    }

    @Override
    public @NotNull Function<MessageModel, MessageEntity> toEntity()
    {
        return messageModel ->
                getRepository()
                        .findById(messageModel.id())
                        .orElseThrow(() -> new EntityUnknownException(messageModel.id()));
    }
}
