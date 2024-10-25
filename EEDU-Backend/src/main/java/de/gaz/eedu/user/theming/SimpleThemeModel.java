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
                ", name='" + name + '\'' +
                ", backgroundColor_r=" + backgroundColor_r +
                ", backgroundColor_g=" + backgroundColor_g +
                ", backgroundColor_b=" + backgroundColor_b +
                ", widgetColor_r=" + widgetColor_r +
                ", widgetColor_g=" + widgetColor_g +
                ", widgetColor_b=" + widgetColor_b +
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
