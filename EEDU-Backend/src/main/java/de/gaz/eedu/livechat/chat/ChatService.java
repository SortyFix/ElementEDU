package de.gaz.eedu.livechat.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.livechat.DTO.WebsocketChatEdit;
import de.gaz.eedu.livechat.DTO.WebsocketMessageCreation;
import de.gaz.eedu.livechat.WSIdentifiers;
import de.gaz.eedu.livechat.message.MessageCreateModel;
import de.gaz.eedu.livechat.message.MessageEntity;
import de.gaz.eedu.livechat.message.MessageModel;
import de.gaz.eedu.livechat.message.MessageService;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.verification.GeneratedToken;
import de.gaz.eedu.user.verification.VerificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
public class ChatService extends EntityService<ChatRepository, ChatEntity, ChatModel, ChatCreateModel>
{
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final VerificationService verificationService;
    private final MessageService messageService;
    private final UserService userService;
    private final WSIdentifiers wsIdentifiers;

    @Override
    public @NotNull ChatRepository getRepository()
    {
        return chatRepository;
    }

    @Override
    public @NotNull List<ChatEntity> createEntity(@NotNull Set<ChatCreateModel> model) throws CreationException
    {
        List<ChatEntity> empty = Collections.emptyList();
        List<ChatEntity> entities = model.stream()
                                         .map(chatCreateModel -> {
                                             boolean noChatroomFound =
                                                     loadEntityByUserIDs(Arrays.stream(chatCreateModel.users())
                                                                               .toList()).equals(Optional.of(empty));
                                             if(!noChatroomFound){
                                                 throw new OccupiedException();
                                             }
                                             return chatCreateModel.toEntity(new ChatEntity());
                                         }).toList();

        return chatRepository.saveAll(entities);
    }

    public @NotNull Optional<List<ChatEntity>> loadEntityByUserIDs(@NotNull List<Long> userIDs){
        return chatRepository.findAllByUsersIn(userIDs, (long) userIDs.size());
    }

    public @NotNull GeneratedToken generateWebsocketToken(long userId) {
        return verificationService.websocketToken(userId);
    }

    /**
     * Creates a new chat name with the specified users.
     *
     * <p>This endpoint requires authentication, and the current user initiating the request is considered as the creator of the chat name.
     * The request body should contain a list of user IDs representing the participants in the chat name. A name must have at least two users to be valid.
     * </p>
     * <p>
     * If the number of users is less than two, the response will be a {@link HttpStatus#FORBIDDEN} status.
     * </p>
     * <p>
     * Checks are performed to ensure the validity of the provided user IDs:
     * <ul>
     *     <li>Verifies that all provided user IDs exist in the system.</li>
     *     <li>Ensures that the current user initiating the request is included in the list of users.</li>
     * </ul>
     * If any of these checks fail, the response will be a {@link HttpStatus#FORBIDDEN} status.
     * </p>
     * <p>
     * Additionally, checks if a chat name already exists with the same set of users. If such a chat name exists, the response will be a {@link HttpStatus#CONFLICT} status.
     * </p>
     * <p>
     * If all validation checks pass, a new chat name is created with the specified users, and a {@link ResponseEntity} containing the created chat name's details is returned with a {@link HttpStatus#OK} status.
     * </p>
     *
     * @param users       A list of user IDs representing the participants in the chat name.
     * @return A {@link ResponseEntity} containing the details of the created chat name if successful, or an appropriate HTTP status indicating the outcome.
     */

    @Transactional
    public @NotNull ChatModel createChat(@NotNull Long userId, @NotNull List<Long> users)
    {
        if(users.size() < 2)
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Optional<List<ChatEntity>> chatMatches = loadEntityByUserIDs(users);

        boolean usersValid = users.stream().allMatch(user -> userService.loadEntityById(user).isPresent());
        boolean noResults = chatMatches.isPresent() && chatMatches.get().isEmpty();

        if(!(noResults && usersValid))
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        ChatCreateModel chatCreateModel = new ChatCreateModel(users.toArray(new Long[0]), System.currentTimeMillis());
        ChatEntity chatEntity = createEntity(Set.of(chatCreateModel)).getFirst();

        return getPersonalizedModel(chatEntity, userId);
    }

    public @NotNull String getChatTitle(@NotNull Long userId, @NotNull List<Long> users)
    {
        List<Long> userList = new ArrayList<>(users);
        userList.remove(userId);
        return userService.loadEntityById(userList.getFirst()).orElseThrow(EntityNotFoundException::new).getFullName();
    }

    public @NotNull ChatModel getPersonalizedModel(@NotNull ChatEntity chatEntity, @NotNull Long userId)
    {
        return new ChatModel(
                chatEntity.getChatId(),
                getChatTitle(userId, chatEntity.getUsers()),
                chatEntity.getTimeOfCreation(),
                chatEntity.getUsers().toArray(new Long[0]),
                chatEntity.getMessages().toArray(new Long[0]));
    }

    /**
     * Handles the sending of a message within a chat identified by the specified chat ID.
     *
     * <p>
     * This endpoint, like the former, requires authentication, and the current user initiating the request is considered as the author of the message.
     * The request body should contain the text content of the message.
     * </p>
     * <p>
     * The method performs the following checks:
     * <ul>
     *     <li>Verifies that the authenticated user is authorized to send messages in the specified chat.</li>
     *     <li>Determines the message status based on the number of users in the chat: GROUP for chats with more than two users,
     *         UNREAD for two-user chats, and returns a {@link HttpStatus#BAD_REQUEST} status if the chat size is invalid.</li>
     *     <li>Creates a new message entity with the provided author ID, message body, timestamp, and message status.</li>
     *     <li>Adds the new message to the chat's list of messages.</li>
     *     <li>Sends the message to the WebSocket topic associated with the chat.</li>
     * </ul>
     * </p>
     * @param json - The json to send via websockets
     * @return A {@link HttpStatus} indicating the outcome of the message sending operation.
     *         Returns {@link HttpStatus#OK} if the operation is successful,
     *         {@link HttpStatus#UNAUTHORIZED} if the user is not authorized to send messages in the chat,
     *         or {@link HttpStatus#BAD_REQUEST} if there's an issue with the chat size.
     */
    @Transactional
    public @NotNull HttpStatus sendMessage(@NotNull String json) throws JsonProcessingException
    {
        WebsocketMessageCreation message = deserializeJSON(json, WebsocketMessageCreation.class);
        UserEntity author = userService.loadEntityById(message.authorId()).orElse(null);

        return loadEntityById(message.chatId()).map(chatEntity -> {
            MessageCreateModel messageCreateModel = null;

            if (!(chatEntity.getUsers().contains(message.authorId()) && Objects.nonNull(author)))
            {
                return HttpStatus.UNAUTHORIZED;
            }

            if(chatEntity.getUsers().size() >= 2)
            {
                messageCreateModel = new MessageCreateModel(message.authorId(), message.body(), System.currentTimeMillis());
            }
            
            if(Objects.isNull(messageCreateModel))
            {
                return HttpStatus.BAD_REQUEST;
            }

            MessageEntity messageEntity = messageService.createEntity(Set.of(messageCreateModel)).getFirst();
            chatEntity.getMessages().add(messageEntity.getMessageId());

            messagingTemplate.
                    convertAndSend(wsIdentifiers.getBroker() + "/" + message.chatId(), messageEntity.toModel());
            String username = getUserService().loadByIdSafe(message.authorId()).loginName();
            messagingTemplate.convertAndSend(wsIdentifiers.getBroker() + "/" + username, message.chatId());

            return HttpStatus.OK;
        }).orElse(HttpStatus.UNAUTHORIZED);
    }

    public <T> T deserializeJSON(@NotNull String json, @NotNull Class<T> dto) throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, dto);
    }

    public String testMessage(@NotNull String string)
    {
        return "Message recieved: " + string;
    }

    /**
     * Retrives chat data of a chat name.
     *
     * <p>
     *     This method fetches information of a chat name (as a {@link ChatModel} containing the chat details like
     *     it's {@code List<MessageEntity>}) by the providedChat ID. It can only be used if an authorized User ID is given.
     *     If unauthorized, the HTTP Status {@code UNAUTHORIZED} will be thrown, preventing third parties from accessing chat information.
     * </p>
     * @return {@code ResponseEntity<ChatModel>} containing chat information.
     * @throws ResponseStatusException with NOT_FOUND if the requested chat could not be found, and UNAUTHORIZED if the given user is not found inside the {@code users} attribute of the ChatEntity.
     */
    @Transactional
    public ChatModel getChatData(@NotNull String json) throws JsonProcessingException
    {
        WebsocketChatEdit dto = deserializeJSON(json, WebsocketChatEdit.class);
        return loadEntityById(dto.chatId()).map(chatEntity -> {
            if(chatEntity.getUsers().contains(dto.userId()))
            {
                return chatEntity.toModel();
            }
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public @NotNull Optional<MessageModel[]> getMessages(@NotNull Long userId, @NotNull Long chatId)
    {
        ChatEntity chatEntity = loadEntityById(chatId).orElseThrow(() -> new EntityUnknownException(chatId));
        return userService.loadEntityById(userId).map(userEntity -> {
            if(chatEntity.getUsers().contains(userEntity.getId()))
            {
                return chatEntity.getMessages().stream().map(id ->
                        messageService.loadEntityById(id).orElseThrow(() -> new EntityUnknownException(chatId)).toModel()).toArray(MessageModel[]::new);
            }
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        });
    }


    public HttpStatus holdMessage(@NotNull Long userId, @NotNull @DestinationVariable Long chatId)
    {
        return messageService.loadEntityById(chatId).map(
                messageEntity -> {
                    if(messageEntity.getAuthor().getId().equals(userId)){
                        if((System.currentTimeMillis() - messageEntity.getTimestamp()) > 20000){
                            messageService.delete(chatId);
                            return HttpStatus.OK;
                        }
                        return HttpStatus.FORBIDDEN;
                    }
                    return HttpStatus.UNAUTHORIZED;
                }).orElse(HttpStatus.NOT_FOUND);
    }

    public List<ChatModel> getChatsFromUser(@NotNull Long userId)
    {
        return getChatRepository()
                .findByUsersContaining(userId)
                .orElse(Collections.emptyList())
                .stream().map(chatEntity -> getPersonalizedModel(chatEntity, userId))
                .collect(Collectors.toList());
    }

}
