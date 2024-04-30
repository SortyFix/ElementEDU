package de.gaz.eedu.user.verfication.twofa.model;

import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.user.verfication.twofa.TwoFactorEntity;
import de.gaz.eedu.user.verfication.twofa.implementations.TwoFactorMethod;
import org.jetbrains.annotations.NotNull;

public record TwoFactorCreateModel(@NotNull Long userID, @NotNull String method,
                                   @NotNull String data) implements CreationModel<TwoFactorEntity>
{
    @Override public @NotNull TwoFactorEntity toEntity(@NotNull TwoFactorEntity entity)
    {
        entity.setEnabled(false);
        entity.setData(data);
        entity.setMethod(TwoFactorMethod.valueOf(method));
        return entity;
    }
}
