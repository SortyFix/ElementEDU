package de.gaz.eedu.course.room;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.gaz.eedu.course.appointment.entry.AppointmentEntryEntity;
import de.gaz.eedu.course.appointment.scheduled.ScheduledAppointmentEntity;
import de.gaz.eedu.course.room.model.RoomModel;
import de.gaz.eedu.entity.model.EntityModelRelation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class RoomEntity implements EntityModelRelation<RoomModel>
{
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id private long id;
    @Column(length = 20, nullable = false)
    private String name;

    @JsonBackReference @OneToMany(mappedBy = "room")
    private final Set<AppointmentEntryEntity> appointments = new HashSet<>();

    @JsonBackReference @OneToMany(mappedBy = "room")
    private final Set<ScheduledAppointmentEntity> scheduledAppointments = new HashSet<>();

    @Contract(pure = true, value = "-> new")
    @Override public @NotNull RoomModel toModel()
    {
        return new RoomModel(getId(), getName());
    }
}
