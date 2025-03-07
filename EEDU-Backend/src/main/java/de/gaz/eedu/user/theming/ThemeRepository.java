package de.gaz.eedu.user.theming;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThemeRepository extends JpaRepository<ThemeEntity, Long>
{
    Optional<ThemeEntity> findByName(@NotNull String name);
}
