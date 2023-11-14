package de.gaz.eedu.user.verfication;

import org.jetbrains.annotations.NotNull;

public record ClaimHolder<T>(@NotNull String key, @NotNull T content) {}
