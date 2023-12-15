package de.gaz.eedu.livechat.chat;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class ChatService implements EntityService<ChatEntity, ChatModel, ChatCreateModel>
{
    ChatRepository chatRepository;

    @Override
    public @NotNull Optional<ChatEntity> loadEntityByID(long id)
    {
        return chatRepository.findById(id);
    }

    @Override
    public @NotNull Optional<ChatEntity> loadEntityByName(@NotNull String name)
    {
        return Optional.empty();
    }

    @Override
    public @Unmodifiable @NotNull List<ChatEntity> findAllEntities()
    {
        return chatRepository.findAll();
    }

    @Override
    public @NotNull ChatEntity createEntity(@NotNull ChatCreateModel model) throws CreationException
    {
        return chatRepository.save(model.toEntity(new ChatEntity()));
    }

    @Override
    public boolean delete(long id)
    {
        chatRepository.findById(id).map(entity ->
        {
           chatRepository.delete(entity);
           return true;
        });
        return false;
    }

    @Override
    public @NotNull ChatEntity saveEntity(@NotNull ChatEntity entity)
    {
        return chatRepository.save(entity);
    }

    @Override
    public @NotNull Function<ChatModel, ChatEntity> toEntity()
    {
        return chatModel -> chatRepository.findById(chatModel.chatId()).orElseThrow(() -> new EntityUnknownException(chatModel.chatId()));
    }

    @Override
    public @NotNull Function<ChatEntity, ChatModel> toModel()
    {
        return ChatEntity::toModel;
    }
}
