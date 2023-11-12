package de.gaz.eedu.user.theming;

import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public record ThemeCreateModel(String name, int backgroundColor, int widgetColor, int textColor) implements CreationModel<ThemeEntity>
{

    @Override
    public @NotNull ThemeEntity toEntity(@NotNull ThemeEntity themeEntity)
    {
        themeEntity.setName(name());
        themeEntity.setBackgroundColor(backgroundColor());
        themeEntity.setWidgetColor(widgetColor());
        themeEntity.setTextColor(textColor());
        themeEntity.setUsers(new HashSet<>());
        return themeEntity;
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString()
    {
        return "ThemeCreateModel[" +
                "name=" + name + ", " +
                "backgroundColor=" + backgroundColor + ", " +
                "widgetColor=" + widgetColor + ", " +
                "textColor=" + textColor + ']';
    }


}
