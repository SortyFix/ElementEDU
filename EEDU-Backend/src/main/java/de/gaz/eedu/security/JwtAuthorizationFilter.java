package de.gaz.eedu.security;

import de.gaz.eedu.user.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j @Order(1) @AllArgsConstructor @Getter(AccessLevel.PROTECTED)
public class JwtAuthorizationFilter extends OncePerRequestFilter
{
    private final UserService userService;

    @Override protected void doFilterInternal(@NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException,
            IOException
    {
        try
        {
            // first check header then cookies
            String token = checkHeader(request).or(() -> checkCookies(request)).orElse("");

            getUserService().validate(token).ifPresentOrElse((auth) ->
            {
                log.info("The authorization token was successfully validated.");
                SecurityContextHolder.getContext().setAuthentication(auth);
                request.setAttribute("token", auth.getDetails());
            }, () -> log.warn("The request did not contain a valid authorization token."));
        } catch (ExpiredJwtException expiredJwtException)
        {
            log.warn("An incoming request had an expired token.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The token is expired");
        }

        filterChain.doFilter(request, response);
    }

    private @NotNull Optional<String> checkHeader(@NotNull HttpServletRequest request)
    {
        String header = request.getHeader("Authorization");
        if (Objects.isNull(header))
        {
            return Optional.empty();
        }
        log.info("Authorization header detected. Proceed to validate token.");
        return Optional.of(header.substring("Bearer ".length()));
    }

    private @NotNull Optional<String> checkCookies(@NotNull HttpServletRequest request)
    {
        Cookie[] cookies = request.getCookies();
        if (Objects.isNull(cookies))
        {
            return Optional.empty();
        }
        log.info("Authorization cookie detected. Proceed to validate token.");
        Predicate<Cookie> isJwtToken = cookie -> Objects.equals("token", cookie.getName());
        return Stream.of(cookies).filter(isJwtToken).findFirst().map(Cookie::getValue);
    }
}
