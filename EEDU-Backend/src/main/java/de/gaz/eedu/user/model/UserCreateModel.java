package de.gaz.eedu.user.model;

import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserStatus;
import org.jetbrains.annotations.NotNull;

public record UserCreateModel(@NotNull String firstName, @NotNull String lastName, @NotNull String loginName, @NotNull String password,
                              @NotNull Boolean enabled, @NotNull Boolean locked, @NotNull UserStatus status, @NotNull Long theme, @NotNull Long[] groups) implements CreationModel<UserEntity>
{

    @Override public @NotNull UserEntity toEntity(@NotNull UserEntity userEntity)
    {
        userEntity.setFirstName(firstName());
        userEntity.setLastName(lastName());
        userEntity.setLoginName(loginName());
        userEntity.setEnabled(enabled());
        userEntity.setLocked(locked());
        userEntity.setStatus(status());
        userEntity.setSystemAccount(false);
        return userEntity;
    }
}
