package de.gaz.eedu.user.privileges;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, String> {

    boolean existsByIdIn(@NotNull Collection<String> id);
}
