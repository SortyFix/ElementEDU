package de.gaz.eedu;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked") public record ArrayTestData<P, T>(P entityID, T @NotNull ... expected) {}
