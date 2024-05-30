package de.gaz.eedu.user.verification.credentials.model;

import de.gaz.eedu.entity.model.Model;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import org.jetbrains.annotations.NotNull;

public record CredentialRequestModel(@NotNull Long userId, @NotNull CredentialMethod credentialMethod) implements Model
{
}
