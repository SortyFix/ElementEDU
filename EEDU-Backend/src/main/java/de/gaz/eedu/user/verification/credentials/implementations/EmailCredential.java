package de.gaz.eedu.user.verification.credentials.implementations;

import de.gaz.eedu.user.verification.credentials.CredentialEntity;
import org.jetbrains.annotations.NotNull;

public class EmailCredential implements Credential
{
    @Override public void creation(@NotNull CredentialEntity credentialEntity)
    {
        credentialEntity.setEnabled(true);
    }

    @Override public boolean verify(@NotNull CredentialEntity credentialEntity, @NotNull String code)
    {
        return false;
    }

    @Override public boolean enable(@NotNull CredentialEntity credentialEntity, @NotNull String code)
    {
        // doesn't matter, keeping it false
        return false;
    }
}
