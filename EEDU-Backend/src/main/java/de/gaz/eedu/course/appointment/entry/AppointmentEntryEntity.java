package de.gaz.eedu.course.appointment.entry;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.gaz.eedu.course.appointment.CourseAppointmentEntity;
import de.gaz.eedu.entity.model.EntityObject;
import de.gaz.eedu.file.FileEntity;
import de.gaz.eedu.file.exception.MaliciousFileException;
import de.gaz.eedu.user.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

@Slf4j @Entity @NoArgsConstructor @Getter @Setter public class AppointmentEntryEntity implements EntityObject
{
    @Setter(AccessLevel.NONE) @Id private long id;
    private String description, homework;
    @ManyToOne @JoinColumn(name = "course_appointment_id") @JsonBackReference
    private CourseAppointmentEntity courseAppointment;

    public AppointmentEntryEntity(long id)
    {
        this.id = id;
    }

    public void uploadHomework(@NotNull UserEntity user, @NotNull MultipartFile... files) throws MaliciousFileException
    {
        if (!getCourseAppointment().getCourse().getUsers().contains(user))
        {
            String warnMessage = "Uploading files from user {} to appointment entry {}. But {} is not a part of the course.";
            log.warn(warnMessage, user.getId(), getId(), user.getId());
        }

        String uploadPath = uploadPath(user);
        FileEntity fileEntity = getCourseAppointment().getCourse().getRepository();
        fileEntity.uploadBatch(uploadPath, files);

        log.info("User {} has uploaded files to appointment entry {}", user.getId(), getId());
    }

    private @NotNull String uploadPath(@NotNull UserEntity user)
    {
        return String.format("%s", user.getId());
    }
}
