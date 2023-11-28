package de.gaz.eedu.user.verfication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

@AllArgsConstructor @Getter public class VerificationAuthority implements GrantedAuthority
{
    private JwtTokenType jwtTokenType;

    @Override public String getAuthority()
    {
        return getJwtTokenType().toString();
    }

    public boolean equals(@NotNull JwtTokenType jwtTokenType)
    {
        return Objects.equals(getJwtTokenType(), jwtTokenType);
    }

    @Override public boolean equals(@Nullable Object o)
    {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        VerificationAuthority that = (VerificationAuthority) o;
        return getJwtTokenType() == that.getJwtTokenType();
    }

    @Override public int hashCode()
    {
        return Objects.hash(getJwtTokenType());
    }
}
