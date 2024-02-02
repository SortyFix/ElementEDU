package de.gaz.eedu.course;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.course.subjects.SubjectEntity;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.model.UserModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Table(name = "course_entity")
public class CourseEntity implements EntityModelRelation<CourseModel>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(AccessLevel.NONE) private Long id; // ID is final
    private String name;
    @ManyToOne @JsonManagedReference @JoinColumn(name = "subject", referencedColumnName = "id") private SubjectEntity subjectEntity;
    @ManyToMany private final Set<UserEntity> users = new HashSet<>();

    @Override
    public CourseModel toModel()
    {
        //TODO
        return new CourseModel(getId(), getName(), getSubjectEntity().toModel(), new UserModel[0]);
    }
}
