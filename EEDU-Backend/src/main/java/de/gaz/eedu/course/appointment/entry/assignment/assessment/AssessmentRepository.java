package de.gaz.eedu.course.appointment.entry.assignment.assessment;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssessmentRepository extends JpaRepository<AssessmentEntity, Long>
{
    @Query("SELECT a FROM AssessmentEntity a LEFT JOIN FETCH a.appointment ap LEFT JOIN FETCH a.user u WHERE a.id = :id")
    @Override @NotNull Optional<AssessmentEntity> findById(@NotNull Long id);
}
