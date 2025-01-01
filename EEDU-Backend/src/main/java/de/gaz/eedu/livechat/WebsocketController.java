package de.gaz.eedu.livechat;

import de.gaz.eedu.livechat.chat.ChatModel;
import de.gaz.eedu.livechat.chat.ChatService;
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

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
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

    @MessageMapping("/{chatId}/send")
    public HttpStatus sendMessage(@AuthenticationPrincipal Long authorId, @NotNull @DestinationVariable Long chatId, @NotNull @RequestBody String body)
    {
        return chatService.sendMessage(authorId, chatId, body);
    }

    @MessageMapping("{chatId}/get")
    public ResponseEntity<ChatModel> getChatData(@AuthenticationPrincipal Long userId, @NotNull @DestinationVariable Long chatId)
    {
        return chatService.getChatData(userId, chatId);
    }

    @MessageMapping("/hold/{messageId]}")
    public HttpStatus holdMessage(@AuthenticationPrincipal Long userId, @NotNull @DestinationVariable Long chatId)
    {
        return chatService.holdMessage(userId, chatId);
    }

    @GetMapping("/getChatList")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatModel>> getChatList(@AuthenticationPrincipal Long userId)
    {
        return ResponseEntity.ok(chatService.getChatsFromUser(userId));
    }
}
