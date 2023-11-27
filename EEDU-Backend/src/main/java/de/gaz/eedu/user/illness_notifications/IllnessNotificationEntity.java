package de.gaz.eedu.user.illness_notifications;

import de.gaz.eedu.entity.model.EDUEntity;
import de.gaz.eedu.entity.model.EntityModelRelation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Setter @Getter @Entity @Table(name="illness_notification_entity") public class IllnessNotificationEntity implements EDUEntity, EntityModelRelation<IllnessNotificationModel>
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(value = AccessLevel.NONE) private Long notificationId;
    private Long userId;
    @Enumerated(EnumType.STRING) private IllnessNotificationStatus status;
    private LocalDate notificationDate;

    @Override
    public IllnessNotificationModel toModel()
    {
        return new IllnessNotificationModel(notificationId, userId, status, notificationDate);
    }
}
