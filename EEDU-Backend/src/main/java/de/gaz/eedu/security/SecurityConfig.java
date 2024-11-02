package de.gaz.eedu.security;

import de.gaz.eedu.user.UserService;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
@EnableMethodSecurity()
@EnableWebMvc
@Configuration
@AllArgsConstructor
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

    @Override
    public void addCorsMappings(@NotNull CorsRegistry registry) {
        String[] methods = {"GET", "POST", "PUT", "DELETE"};
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200")
                .exposedHeaders(HttpHeaders.SET_COOKIE)
                .allowCredentials(true)
                .allowedMethods(methods)
                .maxAge(3600);
    }
}
