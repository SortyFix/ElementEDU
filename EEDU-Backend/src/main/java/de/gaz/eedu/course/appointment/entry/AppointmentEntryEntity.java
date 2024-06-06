package de.gaz.eedu.course.appointment.entry;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.appointment.scheduled.ScheduledAppointmentEntity;
import de.gaz.eedu.entity.model.EntityObject;
import de.gaz.eedu.file.FileEntity;
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
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Slf4j @Entity @NoArgsConstructor @Getter @Setter public class AppointmentEntryEntity implements EntityObject
{
    @Setter(AccessLevel.NONE) @Id private long id;
    private Instant timeStamp;
    private Duration duration;
    private String description, homework;
    private boolean submitHomework;
    private Instant publish;
    // might be null, if submitHome is false, or it should be valid until next appointment
    @Nullable private Instant submitUntil;
    @ManyToOne @JoinColumn(name = "course_appointment_id", nullable = false) @JsonBackReference
    private CourseEntity course;
    @Nullable @ManyToOne @JoinColumn(name = "scheduled_appointment_id") @JsonBackReference
    private ScheduledAppointmentEntity scheduledAppointment;

    public AppointmentEntryEntity(long id)
    {
        this.id = id;
    }

    // method not allowed when submitHomework is false
    // bad gateway when any file is malicious
    // bad request when
    public void uploadHomework(@NotNull UserEntity user, @NotNull MultipartFile... files) throws ResponseStatusException
    {
        check(user);

        String uploadPath = uploadPath(user);
        FileEntity fileEntity = getCourse().getRepository();
        fileEntity.uploadBatch(uploadPath, files);

        log.info("User {} has uploaded files to appointment entry {}", user.getId(), getId());
    }

    private void check(@NotNull UserEntity user) throws ResponseStatusException
    {
        // not required to submit
        if (!isSubmitHomework())
        {
            String errorMessage = String.format("The resource %s does not allow uploading files.", getId());
            IllegalStateException illegalStateException = new IllegalStateException(errorMessage);
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, errorMessage, illegalStateException);
        }

        getSubmitUntil().ifPresent(submitUntil -> {
            // has expired
            if (Instant.now().isAfter(submitUntil))
            {
                String errorMessage = String.format("The resource %s no longer accepts uploading files.", getId());
                throw new ResponseStatusException(HttpStatus.GONE, errorMessage);
            }
        });

        // user not in course (warning)
        if (!getCourse().getUsers().contains(user))
        {
            String warnMessage = "Uploading files from user {} to appointment entry {}. But {} is not a part of the course.";
            log.warn(warnMessage, user.getId(), getId(), user.getId());
        }
    }

    public @NotNull Optional<Instant> getSubmitUntil()
    {
        if(!submitHomework)
        {
            // mustn't submit
            return Optional.empty();
        }

        if(Objects.nonNull(this.submitUntil)) {
            return Optional.of(this.submitUntil);
        }

        if(Objects.nonNull(getScheduledAppointment()))
        {
            // defaults to next appointment
            Instant nextAppointment = getTimeStamp().plus(getScheduledAppointment().getPeriod());
            return Optional.of(nextAppointment);
        }
        return Optional.empty();
    }

    private @NotNull String uploadPath(@NotNull UserEntity user)
    {
        return String.format("%s/%s", getId(), user.getId());
    }
}
