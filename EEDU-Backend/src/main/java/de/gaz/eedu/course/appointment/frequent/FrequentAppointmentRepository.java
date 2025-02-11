package de.gaz.eedu.course.appointment.frequent;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository public interface FrequentAppointmentRepository extends JpaRepository<FrequentAppointmentEntity, Long>
{

    @Query(
            "SELECT s FROM FrequentAppointmentEntity s " +
            "LEFT JOIN FETCH s.room r " +
            "LEFT JOIN FETCH s.course c " +
            "LEFT JOIN FETCH s.entries e " +
            "WHERE s.id = :id"
    )
    @Override @NotNull Optional<FrequentAppointmentEntity> findById(@NotNull Long id);

}
