package de.gaz.eedu.security;

import de.gaz.eedu.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@EnableWebSecurity
@EnableMethodSecurity()
@EnableWebMvc
@Configuration
@AllArgsConstructor
@EnableCaching
public class SecurityConfig implements WebMvcConfigurer
{
    private final UserService userService;

    @Bean public @NotNull SecurityFilterChain filterChain(@NotNull HttpSecurity http) throws Exception
    {
        JwtAuthorizationFilter jwtFilter = new JwtAuthorizationFilter(userService);
        Class<UsernamePasswordAuthenticationFilter> clazz = UsernamePasswordAuthenticationFilter.class;

        HttpSecurity csrf = http.csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults());
        csrf.addFilterBefore(jwtFilter, clazz).authorizeHttpRequests(auth ->
        {
            String user = "/api/v1/user/%s";
            String login = String.format(user, "login"), logout = String.format(user, "logout");
            auth.requestMatchers(login).anonymous().requestMatchers(logout).permitAll().anyRequest().authenticated();
        });

        return http.build();
    }

    @Override public void addCorsMappings(@NotNull CorsRegistry registry)
    {
        String[] methods = {"GET", "POST", "PUT", "DELETE"};
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200") // Ensure no trailing slash
                .allowCredentials(true)
                .allowedMethods(methods)
                .exposedHeaders(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, HttpHeaders.SET_COOKIE)
                .maxAge(3600);
    }

    @Bean
    public @NotNull CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("tokenValidation");
    }

    @CacheEvict(value = "tokenValidation", allEntries = true)
    @Scheduled(fixedRate = 3000)
    public void emptyToken()
    {
        log.info("Clear cached jwt tokens.");
    }

}
