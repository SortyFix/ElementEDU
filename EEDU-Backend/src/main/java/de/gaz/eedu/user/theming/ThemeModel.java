package de.gaz.eedu.user.theming;

import de.gaz.eedu.entity.model.EntityModel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record ThemeModel(@NotNull Long id, @NotNull String name,
                         byte backgroundColorR, byte backgroundColorG, byte backgroundColorB,
                         byte widgetColorR, byte widgetColorG, byte widgetColorB) implements EntityModel<Long>
{
    @Contract(pure = true) @Override public String toString()
    {
        return "ThemeModel{" +
                "id=" + id +
                ", id='" + name + '\'' +
                ", backgroundColor_r=" + backgroundColorR +
                ", backgroundColor_g=" + backgroundColorG +
                ", backgroundColor_b=" + backgroundColorB +
                ", widgetColor_r=" + widgetColorR +
                ", widgetColor_g=" + widgetColorG +
                ", widgetColor_b=" + widgetColorB +
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
