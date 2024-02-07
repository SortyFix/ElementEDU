package de.gaz.eedu;

import org.jetbrains.annotations.NotNull;

public record TestData<T>(long entityID, @NotNull T expected, boolean equalsResult)
{
    public TestData(long entityID, @NotNull T expected)
    {
        this(entityID, expected, true);
    }
}
