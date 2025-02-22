package de.gaz.eedu.course.subject;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, String>
{

    boolean existsByIdIn(@NotNull Collection<String> name);

}
