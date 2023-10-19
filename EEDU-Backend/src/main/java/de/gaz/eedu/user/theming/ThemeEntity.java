package de.gaz.eedu.user.theming;

import de.gaz.eedu.entity.model.EDUEntity;
import de.gaz.eedu.user.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity public class ThemeEntity implements EDUEntity
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(value = AccessLevel.NONE)
    private Long id;
    @OneToOne
    private UserEntity userEntity;
    private String name;
    private int backgroundColor, widgetColor, textColor;
}
