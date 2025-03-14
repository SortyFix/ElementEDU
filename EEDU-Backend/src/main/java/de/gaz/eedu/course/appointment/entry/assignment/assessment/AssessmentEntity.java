package de.gaz.eedu.course.appointment.entry.assignment.assessment;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.gaz.eedu.course.appointment.entry.AppointmentEntryEntity;
import de.gaz.eedu.course.appointment.entry.assignment.assessment.model.AssessmentModel;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.user.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AssessmentEntity implements EntityModelRelation<Long, AssessmentModel>
{
    @Id private @NotNull Long id;
    @JsonManagedReference @ManyToOne @Setter(AccessLevel.NONE)

    @JoinColumn(name = "appointment_id", referencedColumnName = "id", nullable = false)
    private AppointmentEntryEntity appointment;
    @ManyToOne @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false) @Setter(AccessLevel.NONE)
    private UserEntity user;

    @Column(length = 200) private String feedback;
    private Float grade;

    public AssessmentEntity(long id, @NotNull AppointmentEntryEntity appointment, @NotNull UserEntity user)
    {
        this.id = id;
        this.appointment = appointment;
        this.user = user;
    }

    @Override public @NotNull AssessmentModel toModel()
    {
        return new AssessmentModel(getId(), getGrade(), getFeedback());
    }
}
