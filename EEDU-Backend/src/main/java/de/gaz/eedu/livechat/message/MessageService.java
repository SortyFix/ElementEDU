package de.gaz.eedu.livechat.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.livechat.DTO.WebsocketChatEdit;
import de.gaz.eedu.livechat.chat.ChatEntity;
import de.gaz.eedu.livechat.chat.ChatModel;
import de.gaz.eedu.livechat.chat.ChatService;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Service @RequiredArgsConstructor @Getter(AccessLevel.PROTECTED) public class MessageService extends EntityService<MessageRepository, MessageEntity, MessageModel, MessageCreateModel>
{
    @Getter(AccessLevel.NONE)
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final UserService userService;

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
