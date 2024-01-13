package de.gaz.eedu.livechat;

import de.gaz.eedu.livechat.chat.ChatCreateModel;
import de.gaz.eedu.livechat.chat.ChatEntity;
import de.gaz.eedu.livechat.chat.ChatModel;
import de.gaz.eedu.livechat.chat.ChatService;
import de.gaz.eedu.livechat.message.*;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
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
public class WebsocketController
{
    private final ChatService chatService;
    private final MessageService messageService;
    private final UserService userService;

    public WebsocketController(@NotNull ChatService chatService, @NotNull MessageService messageService, @NotNull UserService userService){
        this.chatService = chatService;
        this.messageService = messageService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<ChatModel> createNewChat(@AuthenticationPrincipal Long currentUser,
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
                ChatCreateModel chatCreateModel = new ChatCreateModel(users.toArray(new Long[0]));
                ChatEntity chatEntity = chatService.createEntity(chatCreateModel);
                return ResponseEntity.ok(chatEntity.toModel());
            }

            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @MessageMapping("/send")
    @SendTo("/topic/{chatId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageModel> createMessage(@AuthenticationPrincipal Long authorId, @NotNull @PathVariable Long chatId, @NotNull @RequestBody String body)
    {
        UserEntity author = userService.loadEntityByID(authorId).orElse(null);
        chatService.loadEntityByID(chatId).map(chatEntity -> {
            if (chatEntity.getUsers().contains(authorId) && Objects.nonNull(author))
            {
                MessageCreateModel messageCreateModel =   chatEntity.getUsers().size()  > 2 ? new MessageCreateModel(authorId, body, MessageStatus.GROUP)
                                                        : chatEntity.getUsers().size() == 2 ? new MessageCreateModel(authorId, body, MessageStatus.UNREAD)
                                                        : null;
                if(!Objects.isNull(messageCreateModel)){
                    MessageEntity messageEntity = messageService.createEntity(messageCreateModel);
                    chatEntity.getMessages().add(messageEntity.getMessageId());
                    return ResponseEntity.ok(messageEntity.toModel());
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        });
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @MessageMapping("/load/{chatId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatModel> loadChat(@AuthenticationPrincipal Long userId, @NotNull @PathVariable Long chatId)
    {
        chatService.loadEntityByID(chatId).map(chatEntity -> {
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        });
        return ResponseEntity.notFound().build();
    }

    @MessageMapping("/hold/{messageId]}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> holdMessage(@AuthenticationPrincipal Long userId, @NotNull @PathVariable Long chatId)
    {
        messageService.loadEntityByID(chatId).map(
                messageEntity -> {
                    if(messageEntity.getAuthor().getId().equals(userId)){
                        if((System.currentTimeMillis() - messageEntity.getTimestamp()) > 20000){
                            messageService.delete(chatId);
                            return ResponseEntity.ok(true);
                        }
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
                    }
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
                });
        return ResponseEntity.notFound().build();
    }
}
