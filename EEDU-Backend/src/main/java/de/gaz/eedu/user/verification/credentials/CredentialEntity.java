package de.gaz.eedu.user.verification.credentials;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.gaz.eedu.entity.model.EntityObject;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import de.gaz.eedu.user.verification.credentials.model.CredentialModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity @NoArgsConstructor @AllArgsConstructor @Getter @Setter public class CredentialEntity implements EntityObject,
        EntityModelRelation<CredentialModel>
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(AccessLevel.NONE) private Long id;
    @Enumerated private CredentialMethod method;
    private String data, secret;
    private boolean enabled;
    @ManyToOne(fetch = FetchType.LAZY) @Setter(AccessLevel.NONE) @JsonBackReference @JoinColumn(name = "user_id", nullable = false) private UserEntity user;

    public CredentialEntity(@NotNull UserEntity user)
    {
        this.user = user;
    }

    @Override public CredentialModel toModel()
    {
        return new CredentialModel(getId(), getMethod(), isEnabled());
    }
}
