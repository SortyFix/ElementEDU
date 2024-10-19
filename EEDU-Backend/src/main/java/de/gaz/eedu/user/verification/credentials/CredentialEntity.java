package de.gaz.eedu.user.verification.credentials;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.entity.model.EntityObject;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import de.gaz.eedu.user.verification.credentials.model.CredentialModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CredentialEntity implements EntityObject, EntityModelRelation<CredentialModel>
{
    @Id @Setter(AccessLevel.NONE) private Long id;
    @Enumerated private CredentialMethod method;
    private String data, secret;
    private boolean enabled;
    @Setter(AccessLevel.NONE) private boolean temporary; // once temporary, always temporary!
    @ManyToOne(fetch = FetchType.LAZY) @Setter(AccessLevel.NONE) @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false) private UserEntity user;

    public CredentialEntity(boolean temporary, @NotNull UserEntity user)
    {
        this.user = user;
        this.temporary = temporary;
        if(temporary)
        {
            this.id = (long) Objects.hash(method, user.getId());
            return;
        }
        this.id = (long) Objects.hash(user.getCredentials().size(), user);
    }

    @Override public CredentialModel toModel()
    {
        return new CredentialModel(getId(), getMethod(), isEnabled());
    }
}
