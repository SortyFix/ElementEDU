package de.gaz.eedu.user.model;

import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserStatus;
import jakarta.validation.constraints.NotEmpty;
import org.jetbrains.annotations.NotNull;

public record UserCreateModel(@NotNull String firstName, @NotNull String lastName, @NotNull String loginName,
                              @NotEmpty(message = "Password must not be empty.") @NotNull String password,
                              @NotNull Boolean enabled, @NotNull Boolean locked, @NotNull Long themeId,
                              @NotNull UserStatus status) implements CreationModel<UserEntity>
{
    @Override public @NotNull String name()
    {
        return loginName;
    }

    @Override public @NotNull UserEntity toEntity(@NotNull UserEntity userEntity)
    {
        userEntity.setFirstName(firstName());
        userEntity.setLastName(lastName());
        userEntity.setLoginName(loginName());
        userEntity.setEnabled(enabled());
        userEntity.setLocked(locked());
        userEntity.setStatus(status());
        return userEntity;
    }
}
