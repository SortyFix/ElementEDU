package de.gaz.eedu;

import org.jetbrains.annotations.NotNull;

public record TestData<P, T>(P entityID, @NotNull T expected, boolean equalsResult)
{
    public TestData(P entityID, @NotNull T expected)
    {
        this(entityID, expected, true);
    }
}
