package de.gaz.eedu.user.verification.credentials.model;

import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record TemporaryCredentialCreateModel(long userId, @NotNull CredentialMethod method, @Nullable String data, @NotNull CredentialMethod... allowed)
{
    public int bitMask()
    {
        return CredentialMethod.bitMask(allowed());
    }
}
