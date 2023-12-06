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
import java.util.stream.Collectors;

@Setter @Getter @Entity @Table(name = "theme_entity") public class ThemeEntity implements EntityObject,
        EntityModelRelation<ThemeModel>
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(value = AccessLevel.NONE) private Long id;
    private String name;
    private int backgroundColor, widgetColor, textColor;
    @OneToMany(mappedBy = "themeEntity") @JsonBackReference private Set<UserEntity> users;

    @Contract(pure = true) @NotNull public SimpleThemeModel toSimpleModel()
    {
        return new SimpleThemeModel(getId(), getName(), getBackgroundColor(), getWidgetColor(), getTextColor());
    }

    @Override @Contract(pure = true) public @NotNull ThemeModel toModel()
    {
        return new ThemeModel(getId(), getName(), getBackgroundColor(), getWidgetColor(), getTextColor(),
                getUsers().stream().map(UserEntity::toSimpleModel).collect(Collectors.toSet()));
    }
}
