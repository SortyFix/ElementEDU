package de.gaz.eedu.user.verfication.twofa;

import de.gaz.eedu.entity.model.EDUEntity;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity @NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class TwoFactorEntity implements EDUEntity, EntityModelRelation<TwoFactorModel>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    private TwoFactorMethod method;
    private String data;
    @ManyToOne
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public TwoFactorEntity(@NotNull UserEntity user)
    {
        this.user = user;
    }

    @Override public TwoFactorModel toModel()
    {
        return new TwoFactorModel(id, method);
    }
}
