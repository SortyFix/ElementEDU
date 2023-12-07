package de.gaz.eedu.user.verfication.twofa;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.gaz.eedu.entity.model.EntityObject;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.verfication.twofa.implementations.TwoFactorMethod;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Entity @NoArgsConstructor @AllArgsConstructor @Getter @Setter public class TwoFactorEntity implements EntityObject,
        EntityModelRelation<TwoFactorModel>
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(AccessLevel.NONE) private Long id;
    @Enumerated private TwoFactorMethod method;
    private String data, secret;
    private boolean enabled;
    @ManyToOne @Setter(AccessLevel.NONE) @JsonBackReference @JoinColumn(name = "user_id", nullable = false) private UserEntity user;

    public TwoFactorEntity(@NotNull UserEntity user)
    {
        this.user = user;
    }

    @Override public TwoFactorModel toModel()
    {
        Map<String, String> claims = new HashMap<>();
        if (!isEnabled())
        {
            claims.put("setup", getMethod().getTwoFactorMethodImplementation().creation(this));
        }
        return new TwoFactorModel(getId(), getMethod(), isEnabled(), claims);
    }
}
