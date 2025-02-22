package de.gaz.eedu.user.group;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Set;

public interface GroupRepository extends JpaRepository<GroupEntity, String> {

    boolean existsByIdIn(@NotNull Collection<String> name);

    @Query("SELECT g FROM GroupEntity g LEFT JOIN FETCH g.privileges p") @NotNull Set<GroupEntity> findAllEagerly();

}
