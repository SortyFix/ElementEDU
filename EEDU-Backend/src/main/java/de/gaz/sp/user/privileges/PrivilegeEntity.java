package de.gaz.sp.user.privileges;

import de.gaz.sp.user.group.GroupEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
public class PrivilegeEntity {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE) // ID is final
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "privileges")
    private Set<GroupEntity> groupEntities;
}
