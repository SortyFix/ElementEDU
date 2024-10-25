package de.gaz.eedu.user.verification.credentials;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import de.gaz.eedu.user.verification.credentials.model.CredentialModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CredentialEntity implements EntityModelRelation<CredentialModel>
{
    @Id @Setter(AccessLevel.NONE) private Long id;
    @Enumerated private CredentialMethod method;
    private String data, secret;
    @Nullable @Setter(AccessLevel.NONE) @Getter(AccessLevel.NONE) private Integer allowedMethods;
    private boolean enabled;
    @ManyToOne(fetch = FetchType.LAZY) @Setter(AccessLevel.NONE) @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false) private UserEntity user;

    public CredentialEntity(@NotNull CredentialMethod credentialMethod, @Nullable Integer methodBitMask, @NotNull UserEntity user)
    {
        this.user = user;
        this.method = credentialMethod;
        if (Objects.nonNull(methodBitMask))
        {
            this.id = temporaryId(credentialMethod, getUser().getId());
            this.allowedMethods = methodBitMask;
            return;
        }
        this.id = (long) Objects.hash(user.getCredentials().size(), user);
    }

    @Contract(pure = true) private static long temporaryId(@NotNull CredentialMethod method, long userId)
    {
        return Objects.hash(method, userId);
    }

    @Override public CredentialModel toModel()
    {
        return new CredentialModel(getId(), getMethod(), isEnabled());
    }

    @Override public boolean deleteManagedRelations()
    {
        return getUser().disableCredential(getId());
    }

    public boolean isTemporary()
    {
        return Objects.equals(this.getId(), temporaryId(getMethod(), getUser().getId()));
    }

    public @NotNull CredentialMethod[] allowedMethods()
    {
        if (!isTemporary() || this.allowedMethods == null)
        {
            return new CredentialMethod[0];
        }

        Stream<CredentialMethod> methods = Arrays.stream(CredentialMethod.values());
        return methods.filter(current -> current.contains(this.allowedMethods)).toArray(CredentialMethod[]::new);
    }
}
