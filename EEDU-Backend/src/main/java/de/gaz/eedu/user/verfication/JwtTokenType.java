package de.gaz.eedu.user.verfication;

import de.gaz.eedu.user.verfication.authority.VerificationAuthority;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum JwtTokenType
{
    TWO_FACTOR_SELECTION,
    TWO_FACTOR_PENDING,
    TWO_FACTOR_REQUIRED,
    ADVANCED_AUTHORIZATION, // authorities from actual user and advanced
    AUTHORIZED; // authorities from actual user

    @Contract(pure = true) @Override public @NotNull String toString()
    {
        return name();
    }

    /**
     * Creates a dedicated authority mapped to this toke type.
     * <p>
     * This method creates a {@link VerificationAuthority} which is dedicated to this class.
     * It grants rights to some special requests, which are related to the key type.
     *
     * @return the created {@link VerificationAuthority}
     * @see VerificationAuthority
     */
    @Contract(value = "-> new", pure = true) public @NotNull VerificationAuthority getAuthority()
    {
        return new VerificationAuthority(this);
    }
}
