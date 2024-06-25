package de.gaz.eedu.user.verification.model;

import de.gaz.eedu.user.model.LoginModel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record AdvancedUserLoginModel(@NotNull String loginName) implements LoginModel
{
    @Contract(pure = true) @Override public @NotNull String toString()
    {  // Automatically generated using intellij
        return "AdvancedUserLoginModel{" + "loginName='" + loginName + '\'' + '}';
    }

    @Override public boolean equals(Object o)
    {  // Automatically generated using intellij
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        AdvancedUserLoginModel that = (AdvancedUserLoginModel) o;
        return Objects.equals(loginName, that.loginName);
    }

    @Override public int hashCode()
    {  // Automatically generated using intellij
        return Objects.hashCode(loginName);
    }
}
