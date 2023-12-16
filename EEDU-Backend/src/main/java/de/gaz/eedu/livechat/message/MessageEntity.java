package de.gaz.eedu.livechat.message;

import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.entity.model.EntityObject;
import jakarta.persistence.*;
import lombok.*;

@Entity @AllArgsConstructor @NoArgsConstructor @Builder @Getter @Setter @Table(name = "message_entity")
public class MessageEntity implements EntityObject, EntityModelRelation<MessageModel>
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long messageId;
    private Long authorId;
    private String body;
    private Long timestamp;
    @Enumerated private MessageStatus status;

    @Override
    public MessageModel toModel()
    {
        return new MessageModel(messageId, authorId, body, timestamp, status);
    }
}
