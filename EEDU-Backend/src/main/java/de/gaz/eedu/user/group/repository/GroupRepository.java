package de.gaz.eedu.user.group.repository;

import de.gaz.eedu.user.group.GroupEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long>, GroupEntityRepository
{
    boolean existsByNameIn(@NotNull Collection<String> name);
}
