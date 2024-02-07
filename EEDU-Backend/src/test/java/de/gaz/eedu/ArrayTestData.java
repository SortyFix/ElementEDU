package de.gaz.eedu;

import org.jetbrains.annotations.NotNull;

public record ArrayTestData<T>(long entityID, T @NotNull ... expected) {
    @SafeVarargs public ArrayTestData { }
}
