package de.gaz.eedu.course.classroom;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository public interface ClassRoomRepository extends JpaRepository<ClassRoomEntity, Long>
{

    @Query("SELECT c FROM ClassRoomEntity c LEFT JOIN FETCH c.users LEFT JOIN FETCH c.courses WHERE c.id = :id")
    @Override @NotNull Optional<ClassRoomEntity> findById(@NotNull Long id);

    boolean existsByNameIn(@NotNull Collection<String> name);
}
