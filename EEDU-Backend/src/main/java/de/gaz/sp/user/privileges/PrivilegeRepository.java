package de.gaz.sp.user.privileges;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, Long> {
    @NotNull Optional<PrivilegeEntity> findByName(@NotNull String name);
}
