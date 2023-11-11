package de.gaz.eedu.user.file;

import de.gaz.eedu.entity.model.EDUEntity;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.group.GroupEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity @AllArgsConstructor @NoArgsConstructor @Builder @Table(name = "file_entity")
public class FileEntity implements EDUEntity
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(value = AccessLevel.NONE) private Long id;
    private String fileName;
    private String filePath;
    @ManyToMany @JoinTable(
            name = "file_user_permissions",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> permittedUsers;
    @ManyToMany @JoinTable(
            name = "file_group_permissions",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<GroupEntity> permittedGroups;
    @ElementCollection
    private Set<String> tags;

    public FileModel toModel(){
        return new FileModel(id, fileName, filePath, permittedUsers, permittedGroups, tags);
    }
}
