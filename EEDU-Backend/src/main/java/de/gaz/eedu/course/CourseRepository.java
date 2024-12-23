package de.gaz.eedu.course;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    @Query(
            "SELECT c FROM CourseEntity c " +
            "LEFT JOIN FETCH c.scheduledAppointments sA " +
            "LEFT JOIN FETCH c.appointments a " +
            "LEFT JOIN FETCH c.subject s " +
            "LEFT JOIN FETCH c.users u " +
            "LEFT JOIN FETCH c.classRoom cR " +
            "LEFT JOIN FETCH c.repository r " +
            "WHERE c.id = :id"
    )
    @Override @NotNull Optional<CourseEntity> findById(@NotNull Long id);

    boolean existsByName(@NotNull String name);

    boolean existsByNameIn(@NotNull Collection<String> names);
}
