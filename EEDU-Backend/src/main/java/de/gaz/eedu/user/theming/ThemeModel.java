package de.gaz.eedu.user.theming;

import de.gaz.eedu.entity.model.Model;
import de.gaz.eedu.user.model.UserModel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record ThemeModel(Long id, UserModel userModel, String name, int backgroundColor, int widgetColor, int textColor) implements Model
{
    @Contract(pure = true) @Override public @NotNull String toString()
    {
        return "ThemeModel{" +
                "id=" + id +
                ", userModel=" + userModel +
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
        ThemeModel that = (ThemeModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}
