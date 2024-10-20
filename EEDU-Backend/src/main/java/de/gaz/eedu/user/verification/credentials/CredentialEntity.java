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
import org.jetbrains.annotations.Contract;

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
    @ManyToOne(fetch = FetchType.LAZY) @Setter(AccessLevel.NONE) @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false) private UserEntity user;

    public CredentialEntity(@NotNull CredentialMethod credentialMethod, boolean temporary, @NotNull UserEntity user)
    {
        this.user = user;
        this.method = credentialMethod;
        if (temporary)
        {
            this.id = temporaryId(credentialMethod, getUser().getId());
            return;
        }
        this.id = (long) Objects.hash(user.getCredentials().size(), user);
    }

    @Contract(pure = true)
    private static long temporaryId(@NotNull CredentialMethod method, long userId)
    {
        return Objects.hash(method, userId);
    }

    @Override public CredentialModel toModel()
    {
        return new CredentialModel(getId(), getMethod(), isEnabled());
    }

    public boolean isTemporary()
    {
        return Objects.equals(this.getId(), temporaryId(getMethod(), getUser().getId()));
    }
}
