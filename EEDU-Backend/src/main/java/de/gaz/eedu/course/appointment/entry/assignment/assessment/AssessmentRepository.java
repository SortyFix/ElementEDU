package de.gaz.eedu.course.appointment.entry.assignment.assessment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentRepository extends JpaRepository<AssessmentEntity, Long> {}
