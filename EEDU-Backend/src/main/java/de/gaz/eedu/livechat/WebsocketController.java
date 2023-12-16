package de.gaz.eedu.livechat;

import de.gaz.eedu.livechat.chat.ChatCreateModel;
import de.gaz.eedu.livechat.chat.ChatEntity;
import de.gaz.eedu.livechat.chat.ChatModel;
import de.gaz.eedu.livechat.chat.ChatService;
import de.gaz.eedu.livechat.message.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(name = "/chat")
public class WebsocketController
{
    private final ChatService chatService;
    private final MessageService messageService;

    public WebsocketController(@NotNull ChatService chatService, @NotNull MessageService messageService){
        this.chatService = chatService;
        this.messageService = messageService;
    }

    @PostMapping(name = "/create")
    public ChatModel createNewChat(@NotNull Long[] users){
        ChatCreateModel chatCreateModel = new ChatCreateModel(users);
        ChatEntity chatEntity = chatService.createEntity(chatCreateModel);
        return chatEntity.toModel();
    }

    @MessageMapping("/send")
    @SendTo("{chatId}")
    public ResponseEntity<MessageModel> createMessage(@AuthenticationPrincipal Long authorId, @NotNull @PathVariable Long chatId, @NotNull String body)
    {
        chatService.loadEntityByID(chatId).map(chatEntity -> {
            if (chatEntity.getUsers().contains(authorId)) {
                MessageCreateModel messageCreateModel = new MessageCreateModel(authorId, body);
                MessageEntity messageEntity = messageService.createEntity(messageCreateModel);
                chatEntity.getMessages().add(messageEntity.getMessageId());
                return ResponseEntity.ok(messageEntity.toModel());
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        });
        return ResponseEntity.notFound().build();
    }

    @MessageMapping("/connect/{chatId}")
    public ResponseEntity<ChatModel> loadChat(@AuthenticationPrincipal Long userId, @NotNull @PathVariable Long chatId)
    {
        chatService.loadEntityByID(chatId).map(chatEntity -> {
            if(chatEntity.getUsers().contains(userId))
            {
                return ResponseEntity.ok(chatEntity.toModel());
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        });
        return ResponseEntity.notFound().build();
    }
}
