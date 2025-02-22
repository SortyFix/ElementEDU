package de.gaz.eedu.user.illnessnotifications;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.file.FileEntity;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.illnessnotifications.model.IllnessNotificationModel;
import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Entity @Builder @Table(name="illness_notification_entity")
public class IllnessNotificationEntity implements EntityModelRelation<Long, IllnessNotificationModel>
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(value = AccessLevel.NONE) private Long id;
    @ManyToOne @JoinColumn(name = "user_id") @JsonBackReference private UserEntity user;
    @Enumerated(EnumType.ORDINAL) private IllnessNotificationStatus status;
    private String reason;
    private Long timeStamp;
    private Long expirationTime;
    @OneToOne @JoinColumn(name = "file_entity_id", unique = true, nullable = true) @Nullable private FileEntity fileEntity;

    @Override
    public IllnessNotificationModel toModel()
    {
        return new IllnessNotificationModel(id,
                user.toReducedModel(),
                status,
                reason,
                getTimeStamp(),
                getExpirationTime(),
                Objects.nonNull(fileEntity) ? fileEntity.toModel() : null);
    }
}
