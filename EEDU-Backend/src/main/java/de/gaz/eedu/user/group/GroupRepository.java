package de.gaz.eedu.user.group;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

    @NotNull Optional<GroupEntity> findByName(@NotNull String name);

    boolean existsByName(@NotNull String name);

}
