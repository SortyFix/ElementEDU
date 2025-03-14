package de.gaz.eedu.livechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.gaz.eedu.livechat.chat.ChatModel;
import de.gaz.eedu.livechat.chat.ChatService;
import de.gaz.eedu.livechat.message.MessageModel;
import de.gaz.eedu.livechat.message.MessageService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class WebsocketController
{
    private final ChatService chatService;
    private final MessageService messageService;

    @PostMapping("/create")
    @PreAuthorize("@verificationService.isFullyAuthenticated()")
    public ResponseEntity<ChatModel> createNewRoom(@AuthenticationPrincipal Long userId, @RequestBody @NotNull List<Long> users)
    {
        return ResponseEntity.ok(chatService.createChat(userId, users));
    }

    @MessageMapping("/test")
    @SendTo("/topic/test")
    public String testMessage(@NotNull String body)
    {
        return chatService.testMessage(body);
    }

    @GetMapping("/authenticate")
    public @NotNull ResponseEntity<String> authenticate(@AuthenticationPrincipal long userId)
    {
        return ResponseEntity.ok(chatService.generateWebsocketToken(userId).jwt());
    }

    @MessageMapping("/send")
    @PreAuthorize("@verificationService.isFullyAuthenticated()")
    public HttpStatus sendMessage(@NotNull String json)
    {
        try {
            return chatService.sendMessage(json);
        } catch (JsonProcessingException e) {
            return HttpStatus.BAD_REQUEST;
        }
    }

    @PostMapping("/get/chat")
    @PreAuthorize("@verificationService.isFullyAuthenticated()")
    public ResponseEntity<MessageModel[]> getMessages(@AuthenticationPrincipal Long userId, @NotNull @RequestBody Long chatId)
    {
        return ResponseEntity.ok(chatService.getMessages(userId, chatId).orElse(new MessageModel[]{}));
    }

    @GetMapping("/get")
    @PreAuthorize("@verificationService.isFullyAuthenticated()")
    public ChatModel getChatData(@NotNull String json) throws JsonProcessingException
    {
        return chatService.getChatData(json);
    }

    @MessageMapping("/hold/{messageId]}")
    public HttpStatus holdMessage(@AuthenticationPrincipal Long userId, @NotNull @DestinationVariable Long chatId)
    {
        return chatService.holdMessage(userId, chatId);
    }

    @GetMapping("/getChatList")
    @PreAuthorize("@verificationService.isFullyAuthenticated()")
    public ResponseEntity<List<ChatModel>> getChatList(@AuthenticationPrincipal Long userId)
    {
        return ResponseEntity.ok(chatService.getChatsFromUser(userId));
    }
}
