package de.gaz.eedu.security;

import de.gaz.eedu.user.UserService;
import io.jsonwebtoken.Jwts;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j @Order(1) @AllArgsConstructor @Getter(AccessLevel.PROTECTED)
public class JwtAuthorizationFilter extends OncePerRequestFilter
{

    private final UserService userService;

    private static @NotNull Runnable noToken(@NotNull HttpServletRequest request)
    {
        return () ->
        {
            log.warn("The request did not contain a valid authorization token.");
            request.setAttribute("claims", Jwts.claims().build());
        };
    }

    @Override protected void doFilterInternal(@NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException,
            IOException
    {

        checkHeader(request).or(() -> checkCookies(request)).ifPresentOrElse(token ->
        {
            Consumer<UsernamePasswordAuthenticationToken> tokenConsumer = (usernamePasswordToken) ->
            {
                log.info("The authorization token was successfully validated.");
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordToken);
                request.setAttribute("claims", usernamePasswordToken.getDetails());
            };
            getUserService().validate(token).ifPresentOrElse(tokenConsumer, noToken(request));
        }, noToken(request));

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
