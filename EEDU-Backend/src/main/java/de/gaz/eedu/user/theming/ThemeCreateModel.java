package de.gaz.eedu.user.theming;

import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public record ThemeCreateModel(String name, short[] backgroundColor, short[] widgetColor) implements CreationModel<ThemeEntity>
{

    @Override
    public @NotNull ThemeEntity toEntity(@NotNull ThemeEntity themeEntity)
    {
        themeEntity.setName(name());

        themeEntity.setBackgroundColor_r(backgroundColor[0]);
        themeEntity.setBackgroundColor_g(backgroundColor[1]);
        themeEntity.setBackgroundColor_b(backgroundColor[2]);

        themeEntity.setWidgetColor_r(widgetColor[0]);
        themeEntity.setWidgetColor_g(widgetColor[1]);
        themeEntity.setWidgetColor_b(widgetColor[2]);

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
                "widgetColor=" + widgetColor + ']';
    }


}
