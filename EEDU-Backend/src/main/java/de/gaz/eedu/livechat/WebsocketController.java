package de.gaz.eedu.livechat;

import de.gaz.eedu.livechat.chat.ChatCreateModel;
import de.gaz.eedu.livechat.chat.ChatEntity;
import de.gaz.eedu.livechat.chat.ChatModel;
import de.gaz.eedu.livechat.chat.ChatService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(name = "/chat")
public class WebsocketController
{
    private final ChatService chatService;

    public WebsocketController(@NotNull ChatService chatService){
        this.chatService = chatService;
    }

    @PostMapping(name = "/create")
    public ChatModel createNewChat(@NotNull Long[] users){
        ChatCreateModel chatCreateModel = new ChatCreateModel(users);
        ChatEntity chatEntity = chatService.createEntity(chatCreateModel);
        return chatEntity.toModel();
    }
}
