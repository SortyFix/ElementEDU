package de.gaz.eedu.user.verfication;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum JwtTokenType
{
    TWO_FACTOR_SELECTION,
    TWO_FACTOR_PENDING,
    ADVANCED_AUTHORIZATION, // authorities received from actual user + this one
    AUTHORIZED; // authorities received from actual user


    @Contract(pure = true) @Override public @NotNull String toString()
    {
        return name();
    }

    @Contract(value = "-> new", pure = true) public @NotNull VerificationAuthority getAuthority()
    {
        return new VerificationAuthority(this);
    }
}
