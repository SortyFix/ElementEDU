package de.gaz.eedu.user.privileges;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, String>
{


    @Query("SELECT p FROM PrivilegeEntity p LEFT JOIN FETCH p.groupEntities WHERE p.id = :id")
    @NotNull Optional<PrivilegeEntity> findByIdEagerly(@NotNull String id);

    boolean existsByIdIn(@NotNull Collection<String> id);
}
