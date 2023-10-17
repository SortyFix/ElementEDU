package de.gaz.eedu.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.user.UserEntity;
import jakarta.validation.constraints.NotEmpty;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;

public final class UserCreateModel implements CreationModel<UserEntity>
{
    private final @NotEmpty String firstName;
    private final @NotEmpty String lastName;
    private final @NotEmpty String loginName;
    private final @NotEmpty(message = "Password must not be empty.") String password;
    private final @NotEmpty Boolean enabled;
    private final @NotEmpty Boolean locked;
    @JsonIgnore @Setter private String encryptedPassword;

    public UserCreateModel(@NotEmpty String firstName, @NotEmpty String lastName, @NotEmpty String loginName,
                           @NotEmpty(message = "Password must not be empty.") String password,
                           @NotEmpty Boolean enabled, @NotEmpty Boolean locked)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.loginName = loginName;
        this.password = password;
        this.enabled = enabled;
        this.locked = locked;
    }

    @Override public @NotNull UserEntity toEntity()
    {
        return new UserEntity(null,
                firstName(),
                lastName(),
                loginName(),
                encryptedPassword,
                enabled(),
                locked(),
                new HashSet<>());
    }

    public @NotEmpty String firstName() {return firstName;}

    public @NotEmpty String lastName() {return lastName;}

    public @NotEmpty String loginName() {return loginName;}

    public @NotEmpty(message = "Password must not be empty.") String password() {return password;}

    public @NotEmpty Boolean enabled() {return enabled;}

    public @NotEmpty Boolean locked() {return locked;}

    @Override public boolean equals(Object obj)
    {
        if (obj == this) {return true;}
        if (obj == null || obj.getClass() != this.getClass()) {return false;}
        var that = (UserCreateModel) obj;
        return Objects.equals(this.firstName, that.firstName) && Objects.equals(this.lastName,
                that.lastName) && Objects.equals(this.loginName, that.loginName) && Objects.equals(this.password,
                that.password) && Objects.equals(this.enabled, that.enabled) && Objects.equals(this.locked,
                that.locked);
    }

    @Override public int hashCode()
    {
        return Objects.hash(firstName, lastName, loginName, password, enabled, locked);
    }

    @Contract(pure = true) @Override public @NotNull String toString()
    {
        return "UserCreateModel[" + "firstName=" + firstName + ", " + "lastName=" + lastName + ", " + "loginName=" + loginName + ", " + "password=" + password + ", " + "enabled=" + enabled + ", " + "locked=" + locked + ']';
    }

}
