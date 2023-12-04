package de.gaz.eedu.user.verfication.authority;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;

public record InternalSimpleAuthority(@NotNull String authority) implements GrantedAuthority {
    @Contract(pure = true) @Override public @NotNull String getAuthority()
    {
        return authority();
    }
}
