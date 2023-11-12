package de.gaz.eedu.user.model;

import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.user.UserEntity;
import jakarta.validation.constraints.NotEmpty;
import org.jetbrains.annotations.NotNull;

public record UserCreateModel(@NotEmpty String firstName, @NotEmpty String lastName, @NotEmpty String loginName,
                              @NotEmpty(message = "Password must not be empty.") String password,
                              @NotEmpty Boolean enabled, @NotEmpty Boolean locked, @NotEmpty Long themeId) implements CreationModel<UserEntity>
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
        return userEntity;
    }
}
