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

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatModel> createNewRoom(@AuthenticationPrincipal Long currentUser,
                                                    @RequestBody @NotNull List<Long> users){
        if(users.size() >= 2){
            List<ChatEntity> empty = Collections.emptyList();
            Boolean usersValid = chatService.loadEntityByUserIDs(users).map(chatEntities ->
            {
                AtomicReference<Boolean> allUsersPresent = new AtomicReference<>(true);
                if (!users.contains(currentUser))
                    allUsersPresent.set(false);
                users.forEach(userId ->
                {
                    if (userService.loadEntityByID(userId).isEmpty())
                    {
                        allUsersPresent.set(false);
                    }
                });
                return allUsersPresent.get().equals(true);
            }).orElse(false);

            if(chatService.loadEntityByUserIDs(users).equals(Optional.of(empty)) && usersValid){
                ChatCreateModel chatCreateModel = new ChatCreateModel(users.toArray(new Long[0]), System.currentTimeMillis());
                ChatEntity chatEntity = chatService.createEntity(chatCreateModel);
                return ResponseEntity.ok(chatEntity.toModel());
            }

            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @MessageMapping("/{chatId}/send")
    @PreAuthorize("isAuthenticated()")
    public HttpStatus sendMessage(@AuthenticationPrincipal Long authorId, @NotNull @DestinationVariable Long chatId, @NotNull @RequestBody String body)
    {
        UserEntity author = userService.loadEntityByID(authorId).orElse(null);
        return chatService.loadEntityByID(chatId).map(chatEntity -> {
            if (chatEntity.getUsers().contains(authorId) && Objects.nonNull(author))
            {
                MessageCreateModel messageCreateModel =   chatEntity.getUsers().size()  > 2 ? new MessageCreateModel(authorId, body, MessageStatus.GROUP)
                                                        : chatEntity.getUsers().size() == 2 ? new MessageCreateModel(authorId, body, MessageStatus.UNREAD)
                                                        : null;
                if(Objects.nonNull(messageCreateModel)){
                    MessageEntity messageEntity = messageService.createEntity(messageCreateModel);
                    chatEntity.getMessages().add(messageEntity.getMessageId());
                    messagingTemplate.convertAndSend(WSConfig.getBroker() + "/" + chatId, messageEntity.toModel());
                    return HttpStatus.OK;
                }
                return HttpStatus.BAD_REQUEST;
            }
            return HttpStatus.UNAUTHORIZED;
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
