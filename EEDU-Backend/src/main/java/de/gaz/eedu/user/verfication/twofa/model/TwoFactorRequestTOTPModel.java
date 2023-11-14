package de.gaz.eedu.user.verfication.twofa.model;

import org.jetbrains.annotations.NotNull;

public record TwoFactorRequestTOTPModel(@NotNull String qrCode, @NotNull String secret) implements TwoFactorRequestData {}
