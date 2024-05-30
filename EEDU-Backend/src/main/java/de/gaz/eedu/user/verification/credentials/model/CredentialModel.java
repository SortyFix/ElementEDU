package de.gaz.eedu.user.verification.credentials.model;

import de.gaz.eedu.entity.model.EntityModel;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public record CredentialModel(@NotNull Long id, @NotNull CredentialMethod method, boolean enabled, @Nullable Map<String, String> claims) implements EntityModel
{

    @Contract(pure = true) @Override public @NotNull String toString()
    {
        return "TwoFactorModel{" +
                "id=" + id +
                ", method=" + method +
                ", enabled=" + enabled +
                '}';
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        CredentialModel that = (CredentialModel) o;
        return Objects.equals(id, that.id);
    }

    @Override public int hashCode()
    {
        return Objects.hash(id);
    }
}
