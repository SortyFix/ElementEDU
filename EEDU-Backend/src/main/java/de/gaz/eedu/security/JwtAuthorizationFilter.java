package de.gaz.eedu.security;

import de.gaz.eedu.user.encryption.EncryptionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Order(1)
@AllArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class JwtAuthorizationFilter extends OncePerRequestFilter
{

    private final EncryptionService encryptionService;

    @Override protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException
    {
        String prefix = "Bearer";

        String header = request.getHeader("Authorization");
        if(header != null && header.startsWith(prefix))
        {
            String token = header.substring(prefix.length());
            getEncryptionService().validate(token).ifPresent((usernamePasswordAuthenticationToken -> SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken)));
        }

        filterChain.doFilter(request, response);
    }
}
