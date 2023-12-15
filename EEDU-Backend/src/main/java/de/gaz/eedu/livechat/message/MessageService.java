package de.gaz.eedu.livechat.message;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service public class MessageService
{
    private final MessageRepository messageRepository;

    public MessageService(@NotNull MessageRepository messageRepository){
        this.messageRepository = messageRepository;
    }

    public MessageEntity findEntityById(@NotNull Long id){
        return messageRepository.findMessageEntityByMessageId(id);
    }

    public MessageEntity createEntity(@NotNull MessageCreateModel messageCreateModel)
    {
        return messageCreateModel.toEntity(new MessageEntity());
    }
}
