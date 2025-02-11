package de.gaz.eedu.course.appointment.entry;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository public interface AppointmentEntryRepository extends JpaRepository<AppointmentEntryEntity, Long>
{

    @Query(
            "SELECT a FROM AppointmentEntryEntity a " +
            "LEFT JOIN FETCH a.room r " +
            "LEFT JOIN FETCH a.course c " +
            "WHERE a.id = :id"
    )
    @Override @NotNull Optional<AppointmentEntryEntity> findById(@NotNull Long id);
}
