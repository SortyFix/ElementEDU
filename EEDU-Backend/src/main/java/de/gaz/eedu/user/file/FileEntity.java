package de.gaz.eedu.user.file;

import de.gaz.eedu.entity.model.EDUEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity @AllArgsConstructor @NoArgsConstructor @Builder @Table(name = "file_entity") public class FileEntity implements EDUEntity
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(value = AccessLevel.NONE) private Long id;
    private String fileName;
    private Long authorId;
    private String filePath;
    @ElementCollection @CollectionTable(name = "file_user_permissions", joinColumns = @JoinColumn(name = "file_id")) private Set<Long> permittedUsers;
    @ElementCollection @CollectionTable(name = "file_group_permissions", joinColumns = @JoinColumn(name = "file_id")) private Set<Long> permittedGroups;
    @ElementCollection private Set<String> tags;

    public FileModel toModel()
    {
        return new FileModel(id, fileName, authorId, filePath, permittedUsers, permittedGroups, tags);
    }

    public void setTags(Set<String> tags)
    {
        this.tags = tags;
    }
}
