package de.gaz.eedu.user.privileges;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, Long> {
    @NotNull Optional<PrivilegeEntity> findByName(@NotNull String name);

    boolean existsByName(@NotNull String name);

    boolean existsByNameIn(@NotNull Collection<String> name);

}
