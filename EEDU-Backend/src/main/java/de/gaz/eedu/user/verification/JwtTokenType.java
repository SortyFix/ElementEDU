package de.gaz.eedu.user.verification;

import de.gaz.eedu.user.verification.authority.VerificationAuthority;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum JwtTokenType
{
    CREDENTIAL_SELECTION,
    CREDENTIAL_PENDING,
    CREDENTIAL_REQUIRED,
    CREDENTIAL_CREATION_PENDING,
    ADVANCED_AUTHORIZATION, // authorities from actual user and advanced
    WEBSOCKET,
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
