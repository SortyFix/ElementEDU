package de.gaz.eedu.user;

import de.gaz.eedu.user.group.GroupEntity;
import jakarta.validation.constraints.NotEmpty;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record UserCreateModel(@NotEmpty String firstName, @NotEmpty String lastName,
                              @NotEmpty String loginName,
                              @NotEmpty(message = "Password must not be empty.") String password,
                              @NotEmpty Boolean enabled, @NotEmpty Boolean locked, @NotNull Set<GroupEntity> groupEntities)
{

    /**
     * Turns this Model into a {@link UserEntity}.
     * <p>
     * This method turns this user create model into an {@link UserEntity}.
     * It is used when the user is created in {@link UserService}.
     * <p>
     * Note that this class does not have access to the {@link de.gaz.eedu.user.encryption.EncryptionService} and therefore needs
     * the encrypted password as parameter.
     *
     * @param encryptedPassword the {@link #password()} but encrypted.
     * @return a created {@link UserEntity} which then can be saved.
     * @see UserEntity
     * @see UserService
     */
    public @NotNull UserEntity create(@NotNull String encryptedPassword)
    {
        return new UserEntity(null, firstName(), lastName(), loginName(), encryptedPassword, enabled(), locked(), groupEntities());
    }
}
