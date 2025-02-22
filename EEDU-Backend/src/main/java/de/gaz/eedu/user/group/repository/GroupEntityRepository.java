package de.gaz.eedu.user.group.repository;

import de.gaz.eedu.entity.EntityRepository;
import de.gaz.eedu.user.group.GroupEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface GroupEntityRepository extends EntityRepository<Long, GroupEntity>
{

    @NotNull Optional<GroupEntity> findEntityByName(@NotNull String name);

}
