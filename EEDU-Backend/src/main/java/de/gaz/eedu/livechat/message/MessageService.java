package de.gaz.eedu.livechat.message;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service @RequiredArgsConstructor @Getter(AccessLevel.PROTECTED) public class MessageService implements EntityService<MessageEntity, MessageModel, MessageCreateModel>
{
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageEntity findEntityById(@NotNull Long id){
        return getMessageRepository().findMessageEntityByMessageId(id);
    }

    @Override
    public @NotNull Optional<MessageEntity> loadEntityByID(long id)
    {
        return Optional.of(getMessageRepository().findMessageEntityByMessageId(id));
    }

    @Override
    public @NotNull Optional<MessageEntity> loadEntityByName(@NotNull String name)
    {
        return Optional.empty();
    }

    @Override
    public @Unmodifiable @NotNull List<MessageEntity> findAllEntities()
    {
        return getMessageRepository().findAll();
    }

    public @NotNull MessageEntity createEntity(@NotNull MessageCreateModel messageCreateModel)
    {
        return getMessageRepository().save(messageCreateModel.toMessageEntity(getUserRepository().getReferenceById(
                messageCreateModel.authorId()), new MessageEntity()));
    }

    @Override
    public boolean delete(long id)
    {
        return messageRepository.findById(id).map(messageEntity ->
        {
            messageRepository.deleteById(id);
            return true;
        }).orElse(false);
    }

    @Override
    public @NotNull List<MessageEntity> saveEntity(@NotNull Iterable<MessageEntity> entity)
    {
        return getMessageRepository().saveAll(entity);
    }

    @Override
    public @NotNull Function<MessageModel, MessageEntity> toEntity()
    {
        return messageModel ->
                getMessageRepository()
                        .findById(messageModel.messageId())
                        .orElseThrow(() -> new EntityUnknownException(messageModel.messageId()));
    }

    @Override
    public @NotNull Function<MessageEntity, MessageModel> toModel()
    {
        return MessageEntity::toModel;
    }
}
