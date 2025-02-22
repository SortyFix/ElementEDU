package de.gaz.eedu.user.theming;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record SimpleThemeModel(@NotNull Long id, @NotNull String name)
{
    @Contract(pure = true) @Override public String toString()
    {
        return "SimpleThemeModel{" +
                "id=" + id +
                ", id='" + name;
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleThemeModel that = (SimpleThemeModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}
