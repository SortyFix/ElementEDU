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

import java.util.HashMap;
import java.util.Map;

@Entity @NoArgsConstructor @AllArgsConstructor @Getter @Setter public class CredentialEntity implements EntityObject,
        EntityModelRelation<CredentialModel>
{
    @Id @Setter(AccessLevel.NONE) private Long id;
    @Enumerated private CredentialMethod method;
    private String data, secret;
    private boolean enabled;
    @ManyToOne @Setter(AccessLevel.NONE) @JsonBackReference @JoinColumn(name = "user_id", nullable = false) private UserEntity user;

    public CredentialEntity(long id, @NotNull UserEntity user)
    {
        this.id = id;
        this.user = user;
    }

    @Override public CredentialModel toModel()
    {
        Map<String, String> claims = new HashMap<>();
        if (!isEnabled())
        {
            claims.put("setup", getMethod().getCredential().creation(this));
        }
        return new CredentialModel(getId(), getMethod(), isEnabled(), claims);
    }
}
