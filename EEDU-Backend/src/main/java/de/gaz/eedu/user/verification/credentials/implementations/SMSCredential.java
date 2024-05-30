package de.gaz.eedu.user.verification.credentials.implementations;

import de.gaz.eedu.user.verification.credentials.CredentialEntity;
import org.jetbrains.annotations.NotNull;

public class SMSCredential implements Credential
{
    @Override public @NotNull String creation(@NotNull CredentialEntity credentialEntity)
    {
        return null;
    }

    @Override public boolean verify(@NotNull CredentialEntity credentialEntity, @NotNull String code)
    {
        return false;
    }

    @Override public boolean enable(@NotNull CredentialEntity credentialEntity, @NotNull String code)
    {
        return false;
    }
}
