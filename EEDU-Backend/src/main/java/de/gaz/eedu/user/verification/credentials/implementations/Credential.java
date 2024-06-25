package de.gaz.eedu.user.verification.credentials.implementations;

import de.gaz.eedu.user.verification.credentials.CredentialEntity;
import org.jetbrains.annotations.NotNull;

public interface Credential
{

    @NotNull <T> T creation(@NotNull CredentialEntity credentialEntity);

    boolean verify(@NotNull CredentialEntity credentialEntity, @NotNull String code);

    default boolean enable(@NotNull CredentialEntity credentialEntity, @NotNull String code)
    {
        return true;
    }

}
