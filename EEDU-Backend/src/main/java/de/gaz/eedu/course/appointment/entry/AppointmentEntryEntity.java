package de.gaz.eedu.course.appointment.entry;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.gaz.eedu.course.appointment.CourseAppointmentEntity;
import de.gaz.eedu.entity.model.EntityObject;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Entity @NoArgsConstructor @Getter @Setter public class AppointmentEntryEntity implements EntityObject
{
    @Setter(AccessLevel.NONE) @Id private long id;
    private String description, homework;
    @ManyToOne @JoinColumn(name = "course_appointment_id") @JsonBackReference
    private CourseAppointmentEntity courseAppointment;

    public AppointmentEntryEntity(long id)
    {
        this.id = id;
    }

    public void uploadHomework(@NotNull MultipartFile... files) throws IOException
    {
        //TODO
    }
}
