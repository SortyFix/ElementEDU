package de.gaz.eedu.user.verification.authority;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@FunctionalInterface
public interface AuthorityFactory
{

    @NotNull @Unmodifiable Set<? extends GrantedAuthority> get(long id);

}
