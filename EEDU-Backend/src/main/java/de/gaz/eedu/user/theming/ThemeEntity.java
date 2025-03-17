package de.gaz.eedu.user.theming;

import de.gaz.eedu.entity.model.EntityModelRelation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;


@Setter @Getter @Entity @Table(name = "theme_entity") public class ThemeEntity implements EntityModelRelation<Long, ThemeModel>
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(value = AccessLevel.NONE) private Long id;
    private String name;

    @Column(name = "background_color_r") private byte backgroundColorR;
    @Column(name = "background_color_g") private byte backgroundColorG;
    @Column(name = "background_color_b") private byte backgroundColorB;
    @Column(name = "widget_color_r") private byte widgetColorR;
    @Column(name = "widget_color_g") private byte widgetColorG;
    @Column(name = "widget_color_b") private byte widgetColorB;

    @Override @Contract(pure = true) public @NotNull ThemeModel toModel()
    {
        return new ThemeModel(getId(), getName(),
                getBackgroundColorR(), getBackgroundColorG(), getBackgroundColorB(),
                getWidgetColorR(), getWidgetColorG(), getWidgetColorB());
    }

    @Contract(pure = true) public @NotNull SimpleThemeModel toSimpleModel()
    {
        return new SimpleThemeModel(getId(), getName());
    }
}
