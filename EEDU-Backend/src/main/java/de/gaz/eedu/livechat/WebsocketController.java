package de.gaz.eedu.livechat;

import de.gaz.eedu.livechat.chat.ChatCreateModel;
import de.gaz.eedu.livechat.chat.ChatEntity;
import de.gaz.eedu.livechat.chat.ChatModel;
import de.gaz.eedu.livechat.chat.ChatService;
import de.gaz.eedu.livechat.message.*;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class WebsocketController
{
    private final ChatService chatService;
    private final MessageService messageService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final WSConfig WSConfig;

    /**
     * Creates a new chat room with the specified users.
     *
     * <p>This endpoint requires authentication, and the current user initiating the request is considered as the creator of the chat room.
     * The request body should contain a list of user IDs representing the participants in the chat room. A room must have at least two users to be valid.
     * </p>
     * <p>
     * If the number of users is less than two, the response will be a {@link org.springframework.http.HttpStatus#FORBIDDEN} status.
     * </p>
     * <p>
     * Checks are performed to ensure the validity of the provided user IDs:
     * <ul>
     *     <li>Verifies that all provided user IDs exist in the system.</li>
     *     <li>Ensures that the current user initiating the request is included in the list of users.</li>
     * </ul>
     * If any of these checks fail, the response will be a {@link org.springframework.http.HttpStatus#FORBIDDEN} status.
     * </p>
     * <p>
     * Additionally, checks if a chat room already exists with the same set of users. If such a chat room exists, the response will be a {@link org.springframework.http.HttpStatus#CONFLICT} status.
     * </p>
     * <p>
     * If all validation checks pass, a new chat room is created with the specified users, and a {@link org.springframework.http.ResponseEntity} containing the created chat room's details is returned with a {@link org.springframework.http.HttpStatus#OK} status.
     * </p>
     *
     * @param currentUser The ID of the authenticated user initiating the request.
     * @param users       A list of user IDs representing the participants in the chat room.
     * @return A {@link org.springframework.http.ResponseEntity} containing the details of the created chat room if successful, or an appropriate HTTP status indicating the outcome.
     */
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatModel> createNewRoom(@AuthenticationPrincipal Long currentUser,
                                                    @RequestBody @NotNull List<Long> users)
    {
        if(users.size() < 2)
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Boolean usersValid = chatService.loadEntityByUserIDs(users).map(chatEntities ->
        {
            AtomicReference<Boolean> allUsersPresent = new AtomicReference<>(users.contains(currentUser));

            users.forEach(userId ->
            {
                if (userService.loadEntityByID(userId).isEmpty())
                {
                    allUsersPresent.set(false);
                }
            });

            return allUsersPresent.get().equals(true);
        }).orElse(false);

        if(!chatService.loadEntityByUserIDs(users).isPresent() && usersValid)
        {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        ChatCreateModel chatCreateModel = new ChatCreateModel(users.toArray(new Long[0]), System.currentTimeMillis());
        ChatEntity chatEntity = chatService.createEntity(chatCreateModel);
        return ResponseEntity.ok(chatEntity.toModel());
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
     *         UNREAD for two-user chats, and returns a {@link org.springframework.http.HttpStatus#BAD_REQUEST} status if the chat size is invalid.</li>
     *     <li>Creates a new message entity with the provided author ID, message body, timestamp, and message status.</li>
     *     <li>Adds the new message to the chat's list of messages.</li>
     *     <li>Sends the message to the WebSocket topic associated with the chat.</li>
     * </ul>
     * </p>
     *
     * @param authorId The ID of the authenticated user initiating the request, considered as the author of the message.
     * @param chatId   The ID of the chat to which the message is being sent.
     * @param body     The text content of the message being sent.
     * @return A {@link org.springframework.http.HttpStatus} indicating the outcome of the message sending operation.
     *         Returns {@link org.springframework.http.HttpStatus#OK} if the operation is successful,
     *         {@link org.springframework.http.HttpStatus#UNAUTHORIZED} if the user is not authorized to send messages in the chat,
     *         or {@link org.springframework.http.HttpStatus#BAD_REQUEST} if there's an issue with the chat size.
     */
    @MessageMapping("/{chatId}/send")
    @PreAuthorize("isAuthenticated()")
    public HttpStatus sendMessage(@AuthenticationPrincipal Long authorId, @NotNull @DestinationVariable Long chatId, @NotNull @RequestBody String body)
    {
        UserEntity author = userService.loadEntityByID(authorId).orElse(null);

        return chatService.loadEntityByID(chatId).map(chatEntity -> {

            if (!(chatEntity.getUsers().contains(authorId) && Objects.nonNull(author)))
            {
                return HttpStatus.UNAUTHORIZED;
            }

            MessageCreateModel messageCreateModel =   chatEntity.getUsers().size()  > 2 ? new MessageCreateModel(authorId, body, System.currentTimeMillis(), MessageStatus.GROUP)
                    : chatEntity.getUsers().size() == 2 ? new MessageCreateModel(authorId, body, System.currentTimeMillis(), MessageStatus.UNREAD)
                    : null;
            if(Objects.isNull(messageCreateModel))
            {
                return HttpStatus.BAD_REQUEST;
            }

            MessageEntity messageEntity = messageService.createEntity(messageCreateModel);
            chatEntity.getMessages().add(messageEntity.getMessageId());
            messagingTemplate.convertAndSend(WSConfig.getBroker() + "/" + chatId, messageEntity.toModel());

            return HttpStatus.OK;
        }).orElse(HttpStatus.UNAUTHORIZED);
    }

    @MessageMapping("{chatId}/join")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<? /* Will fix that later, i'm sickkkk */> joinRoom(@AuthenticationPrincipal Long userId, @NotNull @DestinationVariable Long chatId)
    {
        return chatService.loadEntityByID(chatId).map(chatEntity -> {
            if(chatEntity.getUsers().contains(userId))
            {
                chatEntity.getMessages().forEach(
                        messageId -> messageService.loadEntityByID(messageId).ifPresent(messageEntity ->
                        {
                            if(!Objects.equals(messageEntity.getAuthor().getId(), userId) && Objects.equals(messageEntity.getStatus(), MessageStatus.UNREAD)){
                                messageEntity.setStatus(MessageStatus.READ);
                                messageService.save(messageEntity);
                            }
                        }));
                return ResponseEntity.ok(chatEntity.toModel());
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body((ResponseEntity<ChatModel>) null);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body((ResponseEntity<ChatModel>) null));
    }

    @MessageMapping("/hold/{messageId]}")
    @PreAuthorize("isAuthenticated()")
    public HttpStatus holdMessage(@AuthenticationPrincipal Long userId, @NotNull @DestinationVariable Long chatId)
    {
        return messageService.loadEntityByID(chatId).map(
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
}
