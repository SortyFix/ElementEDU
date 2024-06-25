package de.gaz.eedu.user.verification.credentials.model;

import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import org.jetbrains.annotations.NotNull;

public record CredentialAuthModel(@NotNull String code, @NotNull CredentialMethod credentialMethod) {}
