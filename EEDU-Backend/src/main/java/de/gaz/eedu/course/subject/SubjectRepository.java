package de.gaz.eedu.course.subject;

import de.gaz.eedu.course.CourseEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, String>
{
    boolean existsByIdIn(@NotNull Collection<String> name);

    @Query("SELECT c FROM CourseEntity c JOIN FETCH c.subject s WHERE s.id IN :subjects")
    @NotNull @Unmodifiable Collection<CourseEntity> findAllCoursesBySubjectIds(@NotNull Collection<String> subjects);
}
