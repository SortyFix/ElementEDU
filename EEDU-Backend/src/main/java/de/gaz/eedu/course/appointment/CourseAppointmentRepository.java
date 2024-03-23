package de.gaz.eedu.course.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository public interface CourseAppointmentRepository extends JpaRepository<CourseAppointmentEntity, Long> {}
