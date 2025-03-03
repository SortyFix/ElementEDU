package de.gaz.eedu.course.appointment.entry;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.CourseService;
import de.gaz.eedu.course.appointment.AppointmentService;
import de.gaz.eedu.course.appointment.entry.model.AppointmentEntryModel;
import de.gaz.eedu.course.appointment.entry.model.AssignmentCreateModel;
import de.gaz.eedu.course.appointment.entry.model.AssignmentInsightModel;
import de.gaz.eedu.course.appointment.entry.model.AssignmentModel;
import de.gaz.eedu.course.appointment.frequent.FrequentAppointmentEntity;
import de.gaz.eedu.course.room.RoomEntity;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.user.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Entity
@NoArgsConstructor
@Getter
@Setter
public class AppointmentEntryEntity implements EntityModelRelation<Long, AppointmentEntryModel>
{
    @Setter(AccessLevel.NONE) @Id private Long id;
    private Duration duration;
    private Instant publish;
    @Column(name = "description", length = 1000)
    private String description;

    @ManyToOne @JoinColumn(name = "course_appointment_id", nullable = false) @JsonBackReference @Cascade(CascadeType.ALL)
    private CourseEntity course;
    @ManyToOne @JoinColumn(name = "frequent_appointment_id") @JsonBackReference
    private @Nullable FrequentAppointmentEntity frequentAppointment;

    // must be set through extra method to validate integrity
    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.NONE)
    @Nullable private String assignmentDescription;
    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.NONE)
    @Nullable private Instant publishAssignment, submitAssignmentUntil;

    @ManyToOne @JsonManagedReference @JoinColumn(name = "room_id", referencedColumnName = "id")
    private @Nullable RoomEntity room;

    /**
     * This constructor creates a new instance of this entity.
     * <p>
     * Creates a new instance of this class.
     * This is required as the class which this appointment is attached to generates the id and passes it.
     * Normally it's not recommended to use the constructor outside it's use case.
     *
     * @param id the generated id.
     */
    public AppointmentEntryEntity(long id)
    {
        this.id = id;
    }

    public @NotNull Optional<RoomEntity> getRoom()
    {
        FrequentAppointmentEntity frequentAppointment = getFrequentAppointment();
        if (Objects.nonNull(frequentAppointment) && Objects.isNull(room))
        {
            return Optional.of(frequentAppointment.getRoom());
        }
        return Optional.ofNullable(room);
    }

    public @NotNull AssignmentInsightModel getInsight(@NotNull UserEntity user)
    {
        String uploadPath = uploadPath(user.getId());
        File file = new File(uploadPath);
        File[] files = file.listFiles();
        if (!hasSubmitted(user) || !file.isDirectory() || Objects.isNull(files) || files.length == 0)
        {
            return new AssignmentInsightModel(user.getLoginName(), false, new String[0]);
        }

        String[] paths = Arrays.stream(files).map(File::getName).toArray(String[]::new);
        return new AssignmentInsightModel(user.getLoginName(), true, paths);
    }

    // method not allowed when submitHomework is false
    // bad gateway when any file is malicious
    // bad request when
    public void submitAssignment(long user, @NotNull MultipartFile... files) throws ResponseStatusException
    {
        if (!this.isAssignmentValid())
        {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        getCourse().getRepository().uploadBatch(uploadPath(user), files);
        log.info("User {} has uploaded files to appointment entry {}", user, getId());
    }

    public boolean hasSubmitted(@NotNull UserEntity user)
    {
        if (!getCourse().getStudents().contains(user))
        {
            return false;
        }

        return new File(getCourse().getRepository().getFilePath(uploadPath(user.getId()))).exists();
    }

    private @NotNull String uploadPath(long user)
    {
        return String.format("%s/%s", getId(), user);
    }

    @Override public @NotNull AppointmentEntryModel toModel()
    {
        Long attachedScheduled = null;
        if (Objects.nonNull(getFrequentAppointment()))
        {
            attachedScheduled = getFrequentAppointment().getId();
        }

        AssignmentModel assignment = getAssignment().orElse(null);
        return new AppointmentEntryModel(
                getId(),
                attachedScheduled,
                getRoom().map(RoomEntity::toModel).orElse(null),
                this.getDuration().toMillis(),
                this.getDescription(),
                assignment
        );
    }

    @Contract(pure = true, value = "-> new")
    @Override public String toString()
    { // Automatically generated by IntelliJ
        return "AppointmentEntryEntity{" +
                "id=" + id +
                '}';
    }

    @Override public boolean equals(Object o)
    { // Automatically generated by IntelliJ
        if (o == null || getClass() != o.getClass()) {return false;}
        AppointmentEntryEntity that = (AppointmentEntryEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override public int hashCode()
    { // Automatically generated by IntelliJ
        return Objects.hashCode(id);
    }

    public boolean isAssignmentValid()
    {
        Optional<AssignmentModel> assignmentModel = getAssignment();
        if (assignmentModel.isEmpty())
        {
            return false;
        }

        return getAssignment().map(assignment ->
        {
            Instant submitUntil = Instant.ofEpochMilli(assignment.submitUntil());
            Instant publish = Instant.ofEpochMilli(assignment.publish());

            Instant now = Instant.now();
            return now.isBefore(submitUntil) && now.isAfter(publish);
        }).orElse(false);
    }

    public boolean hasAssignment()
    {
        return getAssignment().isPresent();
    }

    /**
     * Validates an assignment, saves it to the entity, and persists the entity to the database, if applicable.
     * <p>
     * This method first validates and sets the assignment details by delegating to {@link #setAssignment(AssignmentCreateModel)}.
     * If the assignment is valid and successfully set, the entity is saved to the database using the provided {@link CourseService}.
     *
     * @param appointmentService the {@link AppointmentService} instance used to persist the entity; must not be {@code null}.
     * @param assignment         the {@link AssignmentCreateModel} containing the assignment details to validate and save, must not be {@code null}.
     * @return {@code true} if the assignment passes validation, is successfully set, and the entity is persisted.{@code false} otherwise.
     * @throws NullPointerException if either {@code courseService} or {@code assignment} is {@code null}.
     */
    public boolean setAssignment(@NotNull AppointmentService appointmentService, @NotNull AssignmentCreateModel assignment)
    {
        if (setAssignment(assignment))
        {
            appointmentService.getEntryRepository().save(this);
            return true;
        }
        return false;
    }

    public boolean unsetAssignment()
    {
        if (!this.hasAssignment())
        {
            return false;
        }
        this.assignmentDescription = null;
        this.publishAssignment = null;
        this.submitAssignmentUntil = null;
        return true;
    }

    /**
     * Validates an assignment and saves it, if applicable.
     * <p>
     * This method checks the values inside the given {@code AssignmentCreateModel} by calling
     * {@link AssignmentCreateModel#validate(long)}. If the values pass validation, they are used to set the new
     * homework-related fields of this entity: {@code assignmentDescription}, {@code publishAssignment},
     * and {@code submitAssignmentUntil}.
     *
     * @param assignment the {@link AssignmentCreateModel} containing the assignment details to validate and save, must not be {@code null}.
     * @return {@code true} if the assignment passes validation and is successfully set, {@code false} otherwise.
     */
    public boolean setAssignment(@NotNull AssignmentCreateModel assignment)
    {
        if (!assignment.validate(getTimeStamp()))
        {
            return false;
        }

        var hash = new Object()
        {
            private int generateHash()
            {
                return Objects.hash(assignmentDescription, publishAssignment, submitAssignmentUntil);
            }
        };

        int hashCode = hash.generateHash();
        this.assignmentDescription = assignment.description();
        this.publishAssignment = assignment.publishInstant();
        this.submitAssignmentUntil = assignment.submitUntilInstant();
        return !Objects.equals(hash.generateHash(), hashCode);
    }

    protected long getTimeStamp()
    {
        return getId() & 0xFFFFFFFFFFFFL;
    }

    /**
     * Retrieves an {@link AssignmentModel} for the current entity, if applicable.
     * <p>
     * This method checks if an assignment can be created based on the following conditions:
     * <ul>
     *     <li>The assignment description must not be {@code null}.</li>
     *     <li>The submission deadline ({@code submitAssignmentUntil}) must not be {@code null}.</li>
     *     <li>The {@code publish} timestamp must not be earlier than the current time.</li>
     * </ul>
     * If any of these conditions are not met, the method returns {@link Optional#empty()}.
     * Otherwise, an {@link AssignmentModel} is created and returned inside an {@link Optional}.
     *
     * @return an {@link Optional} containing an {@link AssignmentModel} if all conditions are satisfied,
     * otherwise, an empty {@link Optional}.
     * @throws NullPointerException if the {@code publish} timestamp is {@code null}, as it is directly dereferenced
     *                              without a null check.
     *                              Example usage:
     *                              <pre>
     *                                                           {@code
     *                                                           Optional<AssignmentModel> assignment = entity.getAssignment();
     *                                                           assignment.ifPresent(a -> {
     *                                                               System.out.println("Assignment Description: " + a.getDescription());
     *                                                               System.out.println("Submission Deadline: " + a.getSubmitUntil());
     *                                                           });
     *                                                           }
     *                                                           </pre>
     */
    public @NotNull Optional<AssignmentModel> getAssignment()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isStudent = authentication.getAuthorities().stream().anyMatch(authority ->
        {
            String authorityName = authority.getAuthority();
            return authority instanceof SimpleGrantedAuthority && Objects.equals(authorityName, "ROLE_student");
        });

        Instant until = this.getSubmitAssignmentUntil();
        Instant publish = this.getPublishAssignment();
        String description = this.getAssignmentDescription();

        boolean invalid = Objects.isNull(description) || Objects.isNull(publish) || Objects.isNull(until);
        if (invalid || (Instant.now().isBefore(publish) && isStudent))
        {
            return Optional.empty();
        }

        return Optional.of(new AssignmentModel(description, publish, until));
    }
}
