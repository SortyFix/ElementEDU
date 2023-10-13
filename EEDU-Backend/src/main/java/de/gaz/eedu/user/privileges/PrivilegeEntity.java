package de.gaz.eedu.user.privileges;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.gaz.eedu.user.group.GroupEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Table(name = "privilege_entity")
public class PrivilegeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // ID is final
    private Long id;
    private String name;
    @JsonBackReference
    @ManyToMany(mappedBy = "privileges")
    private Set<GroupEntity> groupEntities;
}
