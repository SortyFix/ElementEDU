package de.gaz.eedu.user.illnessnotifications;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.entity.model.EntityObject;
import de.gaz.eedu.file.FileEntity;
import de.gaz.eedu.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Entity @Builder @Table(name="illness_notification_entity")
public class IllnessNotificationEntity implements EntityObject, EntityModelRelation<IllnessNotificationModel>
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(value = AccessLevel.NONE) private Long id;
    @ManyToOne @JoinColumn(name = "user_id") @JsonBackReference private UserEntity user;
    @Enumerated(EnumType.ORDINAL) private IllnessNotificationStatus status;
    private String reason;
    private Long timeStamp;
    private Long expirationTime;
    @OneToOne @JoinColumn(name = "file_entity_id", unique = true) private FileEntity fileEntity;

    @Override
    public IllnessNotificationModel toModel()
    {
        return new IllnessNotificationModel(id,
                user.getId(),
                status,
                reason,
                getTimeStamp(),
                getExpirationTime(),
                fileEntity.toModel());
    }
}
