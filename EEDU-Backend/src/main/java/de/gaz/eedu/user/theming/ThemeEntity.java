package de.gaz.eedu.user.theming;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.gaz.eedu.entity.model.EDUEntity;
import de.gaz.eedu.user.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "theme_entity")
public class ThemeEntity implements EDUEntity
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(value = AccessLevel.NONE)
    private Long id;
    private String name;
    private int backgroundColor, widgetColor, textColor;
    @OneToMany(mappedBy = "themeEntity")
    @JsonBackReference
    private Set<UserEntity> users;

    public ThemeModel toModel(){
        return new ThemeModel(
                getId(),
                getName(),
                getBackgroundColor(),
                getWidgetColor(),
                getTextColor()
        );
    }
}
