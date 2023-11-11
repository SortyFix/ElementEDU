package de.gaz.eedu.user.theming;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record SimpleThemeModel(@NotNull Long id, @NotNull String name, int backgroundColor, int widgetColor, int textColor)
{
    @Contract(pure = true) @Override public @NotNull String toString()
    {
        return "SimpleThemeModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", backgroundColor=" + backgroundColor +
                ", widgetColor=" + widgetColor +
                ", textColor=" + textColor +
                '}';
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
