package de.gaz.eedu.user.verification.credentials.model;

import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.user.verification.credentials.CredentialEntity;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record UndefinedCredentialCreateModel(@NotNull CredentialMethod method, boolean temporary,
                                             @Nullable String data) implements CreationModel<CredentialEntity>
{
    @Override public @NotNull CredentialEntity toEntity(@NotNull CredentialEntity entity)
    {
        entity.setEnabled(false);
        entity.setData(data());
        entity.setMethod(method());
        return entity;
    }
}
