package de.gaz.eedu.livechat.chat;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.exception.OccupiedException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ChatService implements EntityService<ChatEntity, ChatModel, ChatCreateModel>
{
    private final ChatRepository chatRepository;

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
        List<ChatEntity> empty = Collections.emptyList();
        boolean noChatroomFound = loadEntityByUserIDs(Arrays.stream(model.users()).toList()).equals(Optional.of(empty));
        if(!noChatroomFound){
            throw new OccupiedException();
        }
        return chatRepository.save(model.toEntity(new ChatEntity()));
    }

    @Override
    public boolean delete(long id)
    {
        return chatRepository.findById(id).map(entity ->
        {
           chatRepository.deleteById(id);
           return true;
        }).orElse(false);
    }

    @Override
    public @NotNull List<ChatEntity> saveEntity(@NotNull Iterable<ChatEntity> entity)
    {
        return chatRepository.saveAll(entity);
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

    public @NotNull Optional<List<ChatEntity>> loadEntityByUserIDs(@NotNull List<Long> userIDs){
        return chatRepository.findAllByUsersIn(userIDs, (long) userIDs.size());
    }

}
