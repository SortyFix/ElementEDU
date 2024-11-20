package de.gaz.eedu.user.verification.credentials.model;

import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.user.verification.credentials.CredentialEntity;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record CredentialCreateModel(@NotNull Long userID, @NotNull CredentialMethod method, @Nullable Integer temporary,
                                    @Nullable String data) implements CreationModel<CredentialEntity>
{

    public CredentialCreateModel(long userId, @NotNull UndefinedCredentialCreateModel model)
    {
        this(userId, model.method(), null, model.data());
    }

    public CredentialCreateModel(long userId, @NotNull TemporaryCredentialCreateModel model)
    {
        this(userId, model.method(), model.bitMask(), model.data());
    }

    @Override public @NotNull CredentialEntity toEntity(@NotNull CredentialEntity entity)
    {
        entity.setEnabled(false);
        entity.setData(data());
        entity.setMethod(method());
        return entity;
    }
}
