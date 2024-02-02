package de.gaz.eedu.course.subjects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.subjects.model.SubjectModel;
import de.gaz.eedu.entity.model.EntityModelRelation;
import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subject_entity")
public class SubjectEntity implements EntityModelRelation<SubjectModel>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(AccessLevel.NONE) private Long id; // ID is final
    private String name;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.REMOVE) // delete courses if subject is deleted.
    @JsonBackReference
    private final Set<CourseEntity> courses = new HashSet<>();

    @Override
    public @NotNull SubjectModel toModel()
    {
        return new SubjectModel(getId(), getName());
    }
}
