package de.gaz.eedu.user.theming;

import de.gaz.eedu.entity.model.EntityModel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record ThemeModel(@NotNull Long id, @NotNull String name, int backgroundColor_r, int backgroundColor_g, int backgroundColor_b,
                         int widgetColor_r, int widgetColor_g, int widgetColor_b) implements EntityModel
{
    @Contract(pure = true) @Override public String toString()
    {
        return "ThemeModel{" +
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
        ThemeModel that = (ThemeModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}
