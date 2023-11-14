package de.gaz.eedu.user.verfication;

import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter(AccessLevel.PRIVATE) public enum JwtTokenType
{
    TWO_FACTOR("two_factor"),
    TWO_FACTOR_PENDING("two_factor_pending"),
    TWO_FACTOR_CREATE("two_factor_create"),
    ADVANCED_AUTHORIZATION("advanced_user_management"), // authorities received from actual user + this one
    AUTHORIZED(""); // authorities received from actual user

    private final String authorityName;

    JwtTokenType(@NotNull String authorityName)
    {
        this.authorityName = "$" + authorityName.toUpperCase();
    }

    @Contract(pure = true) @Override public @NotNull String toString()
    {
        return authorityName;
    }

    @Contract(value = "-> new", pure = true) public @NotNull GrantedAuthority getAuthority()
    {
        return new SimpleGrantedAuthority(authorityName);
    }
}
