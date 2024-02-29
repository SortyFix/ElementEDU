package de.gaz.eedu;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked") public record ArrayTestData<T>(long entityID, T @NotNull ... expected)
{
}
