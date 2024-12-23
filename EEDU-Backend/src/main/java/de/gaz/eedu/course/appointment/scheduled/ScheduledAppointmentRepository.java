package de.gaz.eedu.course.appointment.scheduled;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository public interface ScheduledAppointmentRepository extends JpaRepository<ScheduledAppointmentEntity, Long>
{

    @Query(
            "SELECT s FROM ScheduledAppointmentEntity s " +
            "LEFT JOIN FETCH s.room r " +
            "LEFT JOIN FETCH s.course c " +
            "LEFT JOIN FETCH s.entries e " +
            "WHERE s.id = :id"
    )
    @Override @NotNull Optional<ScheduledAppointmentEntity> findById(@NotNull Long id);

}
