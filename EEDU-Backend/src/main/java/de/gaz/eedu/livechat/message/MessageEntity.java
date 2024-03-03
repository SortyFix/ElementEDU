package de.gaz.eedu.livechat.message;

import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.entity.model.EntityObject;
import de.gaz.eedu.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity @AllArgsConstructor @NoArgsConstructor @Builder @Getter @Setter @Table(name = "message_entity")
public class MessageEntity implements EntityObject, EntityModelRelation<MessageModel>
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long messageId;
    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private UserEntity author;
    private String body;
    private Long timestamp;

    @Override
    public MessageModel toModel()
    {
        return new MessageModel(messageId, author.getId(), body, timestamp);
    }
}
