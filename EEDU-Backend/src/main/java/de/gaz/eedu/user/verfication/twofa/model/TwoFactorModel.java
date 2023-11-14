package de.gaz.eedu.user.verfication.twofa.model;

import de.gaz.eedu.entity.model.Model;
import de.gaz.eedu.user.verfication.twofa.TwoFactorMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record TwoFactorModel(@NotNull Long id, @NotNull TwoFactorMethod twoFactorMethod) implements Model
{

    @Contract(pure = true) @Override public @NotNull String toString()
    {
        return "TwoFactorModel{" +
                "id=" + id +
                ", twoFactorMethod=" + twoFactorMethod +
                '}';
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        TwoFactorModel that = (TwoFactorModel) o;
        return Objects.equals(id, that.id);
    }

    @Override public int hashCode()
    {
        return Objects.hash(id);
    }
}
