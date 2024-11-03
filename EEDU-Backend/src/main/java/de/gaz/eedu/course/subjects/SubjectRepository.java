package de.gaz.eedu.course.subjects;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, Long>
{

    @NotNull Optional<SubjectEntity> findByName(@NotNull String name);

    boolean existsByName(@NotNull String name);

    boolean existsByNameIn(@NotNull Collection<String> name);

}
