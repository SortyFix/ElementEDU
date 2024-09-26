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
    private short backgroundColor_r, backgroundColor_g, backgroundColor_b;
    private short widgetColor_r, widgetColor_g, widgetColor_b;
    @OneToMany(mappedBy = "themeEntity", cascade = {
            CascadeType.REFRESH,
            CascadeType.PERSIST
    }) @JsonBackReference private Set<UserEntity> users;

    @Override @Contract(pure = true) public @NotNull ThemeModel toModel()
    {
        return new ThemeModel(getId(), getName(), getBackgroundColor_r(), getBackgroundColor_g(), getBackgroundColor_b(),
                getWidgetColor_r(), getWidgetColor_g(), getWidgetColor_b());
    }
}
