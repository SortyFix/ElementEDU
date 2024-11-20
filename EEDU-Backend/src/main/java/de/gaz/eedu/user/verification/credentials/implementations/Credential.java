package de.gaz.eedu.user.verification.credentials.implementations;

import de.gaz.eedu.user.verification.credentials.CredentialEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Credential
{

    void creation(@NotNull CredentialEntity credentialEntity);

    boolean verify(@NotNull CredentialEntity credentialEntity, @NotNull String code);

    default boolean enable(@NotNull CredentialEntity credentialEntity, @NotNull String code)
    {
        return true;
    }

    default <T> @Nullable T getSetupData(@NotNull CredentialEntity credentialEntity)
    {
        return null;
    }
}
