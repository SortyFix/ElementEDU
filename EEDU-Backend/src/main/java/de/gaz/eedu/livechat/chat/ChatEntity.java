package de.gaz.eedu.livechat.chat;

import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.entity.model.EntityObject;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity @AllArgsConstructor @NoArgsConstructor @Getter @Setter @Table(name = "chat_entity")
public class ChatEntity implements EntityModelRelation<Long, ChatModel>
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long timeOfCreation;

    @ElementCollection @Column(name = "user_id")
    @CollectionTable(name = "chat_entity_users", joinColumns = @JoinColumn(name = "chat_id"))
    private List<Long> users;

    @ElementCollection @Column(name = "message_id")
    @CollectionTable(name = "chat_entity_messages", joinColumns = @JoinColumn(name = "chat_id"))
    private List<Long> messages;

    // Do not use
    @Override
    public ChatModel toModel()
    {
        return new ChatModel(id, "", timeOfCreation, users.toArray(Long[]::new),
                messages.toArray(Long[]::new));
    }
}
