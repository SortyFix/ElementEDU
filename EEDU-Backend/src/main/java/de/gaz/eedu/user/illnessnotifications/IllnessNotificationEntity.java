package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.entity.model.EntityObject;
import de.gaz.eedu.entity.model.EntityModelRelation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Entity @Builder @Table(name="illness_notification_entity")
public class IllnessNotificationEntity implements EntityObject, EntityModelRelation<IllnessNotificationModel>
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(value = AccessLevel.NONE) private Long notificationId;
    private Long userId;
    @Enumerated(EnumType.STRING) private IllnessNotificationStatus status;
    private LocalDate notificationDate;
    private String reason;

    @Override
    public IllnessNotificationModel toModel()
    {
        return new IllnessNotificationModel(notificationId, userId, status, notificationDate, reason);
    }
}
