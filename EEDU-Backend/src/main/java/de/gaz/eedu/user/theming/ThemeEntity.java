package de.gaz.eedu.user.theming;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.gaz.eedu.entity.model.EntityObject;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.user.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Setter @Getter @Entity @Table(name = "theme_entity") public class ThemeEntity implements EntityObject,
        EntityModelRelation<ThemeModel>
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(value = AccessLevel.NONE) private Long id;
    private String name;
    private byte backgroundColorR, backgroundColorG, backgroundColorB;
    private byte widgetColorR, widgetColorG, widgetColorB;
    @OneToMany(mappedBy = "themeEntity", cascade = {
            CascadeType.REFRESH,
            CascadeType.PERSIST
    }) @JsonBackReference private Set<UserEntity> users;

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
