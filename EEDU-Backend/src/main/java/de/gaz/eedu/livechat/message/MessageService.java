package de.gaz.eedu.livechat.message;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.EntityUnknownException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service public class MessageService implements EntityService<MessageEntity, MessageModel, MessageCreateModel>
{
    private final MessageRepository messageRepository;

    public MessageService(@NotNull MessageRepository messageRepository){
        this.messageRepository = messageRepository;
    }

    public MessageEntity findEntityById(@NotNull Long id){
        return messageRepository.findMessageEntityByMessageId(id);
    }

    @Override
    public @NotNull Optional<MessageEntity> loadEntityByID(long id)
    {
        return Optional.of(messageRepository.findMessageEntityByMessageId(id));
    }

    @Override
    public @NotNull Optional<MessageEntity> loadEntityByName(@NotNull String name)
    {
        return Optional.empty();
    }

    @Override
    public @Unmodifiable @NotNull List<MessageEntity> findAllEntities()
    {
        return messageRepository.findAll();
    }

    public MessageEntity createEntity(@NotNull MessageCreateModel messageCreateModel)
    {
        return messageCreateModel.toEntity(new MessageEntity());
    }

    @Override
    public boolean delete(long id)
    {
        messageRepository.findById(id).map(messageEntity -> {
            messageRepository.delete(messageEntity);
            return true;
        });
        return false;
    }

    @Override
    public @NotNull MessageEntity saveEntity(@NotNull MessageEntity entity)
    {
        return messageRepository.save(entity);
    }

    @Override
    public @NotNull Function<MessageModel, MessageEntity> toEntity()
    {
        return messageModel -> messageRepository.findById(messageModel.messageId()).orElseThrow(() -> new EntityUnknownException(messageModel.messageId()));
    }

    @Override
    public @NotNull Function<MessageEntity, MessageModel> toModel()
    {
        return MessageEntity::toModel;
    }
}
