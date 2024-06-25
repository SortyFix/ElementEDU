package de.gaz.eedu.user.verification.credentials.model;

import org.jetbrains.annotations.NotNull;

public record CredentialTOTPModel(@NotNull String qrCode, @NotNull String secret) implements CredentialRequestData {}
