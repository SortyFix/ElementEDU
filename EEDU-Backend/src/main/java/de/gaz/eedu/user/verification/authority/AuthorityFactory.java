package de.gaz.eedu.user.verification.authority;

import de.gaz.eedu.user.UserEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@FunctionalInterface
public interface AuthorityFactory
{

    @NotNull @Unmodifiable UserEntity get(long id);

}
