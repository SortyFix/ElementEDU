package de.gaz.eedu.user.theming;

import de.gaz.eedu.entity.model.EDUEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class ThemeEntity implements EDUEntity
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(value = AccessLevel.NONE)
    private Long id;
    private String name;
    private int backgroundColor, widgetColor, textColor;

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
