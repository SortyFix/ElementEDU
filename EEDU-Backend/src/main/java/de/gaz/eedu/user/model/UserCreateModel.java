package de.gaz.eedu.user.model;

import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserStatus;
import de.gaz.eedu.user.theming.ThemeCreateModel;
import de.gaz.eedu.user.theming.ThemeEntity;
import jakarta.validation.constraints.NotEmpty;
import org.jetbrains.annotations.NotNull;

public record UserCreateModel(@NotNull String firstName, @NotNull String lastName, @NotNull String loginName,
                              @NotEmpty(message = "Password must not be empty.") @NotNull String password,
                              @NotNull Boolean enabled, @NotNull Boolean locked, @NotNull UserStatus status) implements CreationModel<UserEntity>
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
        userEntity.setThemeEntity(new ThemeCreateModel("default " + System.currentTimeMillis(), 0x050033, 0xb0abd6, 0x000000).toEntity(new ThemeEntity()));
        userEntity.setStatus(status());
        return userEntity;
    }
}
