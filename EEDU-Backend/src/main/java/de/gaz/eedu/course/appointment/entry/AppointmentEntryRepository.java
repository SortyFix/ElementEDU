package de.gaz.eedu.course.appointment.entry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository public interface AppointmentEntryRepository extends JpaRepository<AppointmentEntryEntity, Long>
{}
