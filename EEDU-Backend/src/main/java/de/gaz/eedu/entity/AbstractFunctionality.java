package de.gaz.eedu.entity;

import de.gaz.eedu.exception.EntityUnknownException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class AbstractFunctionality
{

    protected static @NotNull Supplier<EntityUnknownException> entityUnknown(@NotNull Object primaryKey)
    {
        return () -> new EntityUnknownException(primaryKey);
    }

    protected boolean hasAuthority(@NotNull String authority)
    {
        return hasAuthority(authority, SimpleGrantedAuthority.class);
    }

    protected boolean hasAnyAuthority(@NotNull String... authority)
    {
        return Stream.of(authority).anyMatch(this::hasAuthority);
    }

    protected boolean hasRole(@NotNull String role)
    {
        return hasAuthority("ROLE_" + role.toLowerCase(), SimpleGrantedAuthority.class);
    }

    protected boolean hasAnyRole(@NotNull String... role)
    {
        return Stream.of(role).anyMatch(this::hasRole);
    }

    protected boolean hasAuthority(@NotNull String authority, @NotNull Class<? extends GrantedAuthority> parent)
    {
        return getAuthentication().getAuthorities()
                .stream()
                .filter(grantedAuthority ->
                {
                    Class<? extends GrantedAuthority> clazz = grantedAuthority.getClass();
                    return parent.isAssignableFrom(clazz);
                })
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }

    protected @NotNull Authentication getAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    protected void validate(boolean condition)
    {
        validate(condition, new IllegalStateException());
    }

    protected void validate(boolean condition, @NotNull RuntimeException runtimeException)
    {
        validate(condition, () -> runtimeException);
    }

    protected void validate(boolean condition, @NotNull Supplier<? extends RuntimeException> exception)
    {
        if (!condition)
        {
            throw exception.get();
        }
    }

    protected @NotNull ResponseStatusException unauthorizedThrowable()
    {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    protected @NotNull ResponseStatusException forbiddenThrowable()
    {
        return new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
}
