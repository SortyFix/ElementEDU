package de.gaz.eedu.user.group.repository;

import de.gaz.eedu.user.group.GroupEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface GroupRepository extends JpaRepository<GroupEntity, Long>, GroupRepositoryOverride {

    boolean existsByName(@NotNull String name);

    boolean existsByNameIn(@NotNull Collection<String> name);
}
