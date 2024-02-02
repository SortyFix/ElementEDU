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
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "course_entity")
public class CourseEntity implements EntityModelRelation<CourseModel>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(AccessLevel.NONE) private Long id; // ID is final
    private String name;
    @ManyToMany
    @JsonManagedReference
    @JoinTable(name = "course_users", joinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private final Set<UserEntity> users = new HashSet<>();
    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "subject_id", referencedColumnName = "id")
    private SubjectEntity subject;

    @Override
    public CourseModel toModel()
    {
        //TODO
        return new CourseModel(getId(), getName(), getSubject().toModel(), new UserModel[0]);
    }
}
