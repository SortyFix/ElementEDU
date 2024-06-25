package de.gaz.eedu.user.verification.model;

import de.gaz.eedu.user.model.LoginModel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record UserLoginModel(@NotNull String loginName, @NotNull Boolean keepLoggedIn) implements LoginModel
{

    @Contract(pure = true) @Override public @NotNull String toString()
    {  // Automatically generated using intellij
        return "UserLoginModel{" +
                "loginName='" + loginName + '\'' +
                ", keepLoggedIn=" + keepLoggedIn +
                '}';
    }

    @Override public boolean equals(Object o)
    {  // Automatically generated using intellij
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLoginModel that = (UserLoginModel) o;
        return Objects.equals(loginName, that.loginName);
    }

    @Override
    public int hashCode()
    {  // Automatically generated using intellij
        return Objects.hashCode(loginName);
    }
}
