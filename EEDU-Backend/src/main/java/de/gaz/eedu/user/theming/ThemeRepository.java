package de.gaz.eedu.user.theming;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThemeRepository extends JpaRepository<ThemeEntity, Long>
{
    Optional<ThemeEntity> findByName(String name);
}
