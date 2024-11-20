package de.gaz.eedu.course;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    boolean existsByName(@NotNull String name);

    boolean existsByNameIn(@NotNull Collection<String> names);
}
