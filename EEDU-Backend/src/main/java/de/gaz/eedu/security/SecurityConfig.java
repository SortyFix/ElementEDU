package de.gaz.eedu.security;

import de.gaz.eedu.user.UserService;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity @EnableMethodSecurity @EnableWebMvc @Configuration @AllArgsConstructor public class SecurityConfig
{

    private final UserService userService;

    @Bean public @NotNull SecurityFilterChain filterChain(@NotNull HttpSecurity http) throws Exception
    {
        http.csrf(AbstractHttpConfigurer::disable).addFilterBefore(new JwtAuthorizationFilter(userService),
                UsernamePasswordAuthenticationFilter.class).authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    @Bean public @NotNull WebMvcConfigurer corsMappingConfigurer()
    {
        return new WebMvcConfigurer()
        {
            @Override public void addCorsMappings(@NotNull CorsRegistry registry)
            {
                registry.addMapping("/").allowedOrigins("*"); //TODO change in production
            }
        };
    }
}
