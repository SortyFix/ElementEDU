package de.gaz.eedu.user.group;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

    @NotNull Optional<GroupEntity> findByName(@NotNull String name);

    boolean existsByName(@NotNull String name);

    boolean existsByNameIn(@NotNull Collection<String> name);

    @Query("SELECT g FROM GroupEntity g LEFT JOIN FETCH g.privileges p")
    @NotNull Set<GroupEntity> findAllEagerly();

}
