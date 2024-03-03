package de.gaz.eedu.livechat;

import de.gaz.eedu.livechat.chat.ChatModel;
import de.gaz.eedu.livechat.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class WebsocketController
{
    private final ChatService chatService;

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatModel> createNewRoom(@AuthenticationPrincipal Long currentUser,
                                                    @RequestBody @NotNull List<Long> users)
    {
        return ResponseEntity.ok(chatService.createChat(users));
    }

    @MessageMapping("/{chatId}/send")
    @PreAuthorize("isAuthenticated()")
    public HttpStatus sendMessage(@AuthenticationPrincipal Long authorId, @NotNull @DestinationVariable Long chatId, @NotNull @RequestBody String body)
    {
        return chatService.sendMessage(authorId, chatId, body);
    }

    @MessageMapping("{chatId}/get")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatModel> getChatData(@AuthenticationPrincipal Long userId, @NotNull @DestinationVariable Long chatId)
    {
        return chatService.getChatData(userId, chatId);
    }

    @MessageMapping("/hold/{messageId]}")
    @PreAuthorize("isAuthenticated()")
    public HttpStatus holdMessage(@AuthenticationPrincipal Long userId, @NotNull @DestinationVariable Long chatId)
    {
        return chatService.holdMessage(userId, chatId);
    }
}
