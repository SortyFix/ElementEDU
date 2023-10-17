package de.gaz.eedu.user.theming;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface ThemeRepository extends JpaRepository<ThemeEntity, Long>
{
    Optional<ThemeEntity> findByName(String name);
}
