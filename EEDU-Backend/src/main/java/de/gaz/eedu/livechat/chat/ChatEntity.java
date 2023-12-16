package de.gaz.eedu.livechat.chat;

import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.entity.model.EntityObject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity @AllArgsConstructor @NoArgsConstructor @Getter @Setter @Table(name = "chat_entity")
public class ChatEntity implements EntityObject, EntityModelRelation<ChatModel>
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long chatId;

    @ElementCollection @Column(name = "user_id")
    @CollectionTable(name = "chat_entity_users", joinColumns = @JoinColumn(name = "chat_id"))
    private List<Long> users;

    @ElementCollection @Column(name = "message_id")
    @CollectionTable(name = "chat_entity_messages", joinColumns = @JoinColumn(name = "chat_id"))
    private List<Long> messages;

    @Override
    public ChatModel toModel()
    {
        return new ChatModel(chatId, users.toArray(Long[]::new),
                messages.toArray(Long[]::new));
    }
}
