package de.gaz.eedu.user.theming;

import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;

public record ThemeCreateModel(String name, byte[] backgroundColor, byte[] widgetColor) implements CreationModel<ThemeEntity>
{

    @Override
    public @NotNull ThemeEntity toEntity(@NotNull ThemeEntity themeEntity)
    {
        themeEntity.setName(name());

        themeEntity.setBackgroundColorR(backgroundColor[0]);
        themeEntity.setBackgroundColorG(backgroundColor[1]);
        themeEntity.setBackgroundColorB(backgroundColor[2]);

        themeEntity.setWidgetColorR(widgetColor[0]);
        themeEntity.setWidgetColorG(widgetColor[1]);
        themeEntity.setWidgetColorB(widgetColor[2]);

        themeEntity.setUsers(new HashSet<>());
        return themeEntity;
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString()
    {
        return "ThemeCreateModel[" +
                "name=" + name + ", " +
                "backgroundColor=" + Arrays.toString(backgroundColor) + ", " +
                "widgetColor=" + Arrays.toString(widgetColor) + ']';
    }


}
