package de.gaz.eedu.course.classroom;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoomEntity, Long>
{

    @NotNull
    Optional<ClassRoomEntity> findByName(@NotNull String className);

    boolean existsByName(@NotNull String name);

}
