package de.gaz.eedu.course.appointment.scheduled;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository public interface ScheduledAppointmentRepository extends JpaRepository<ScheduledAppointmentEntity, Long> {}
