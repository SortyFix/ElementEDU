package de.gaz.eedu.course.room;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long>
{

    boolean existsByNameIn(@NotNull Collection<String> name);

}
