package de.gaz.eedu.file;

import de.gaz.eedu.entity.model.EntityModelRelation;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity @AllArgsConstructor @NoArgsConstructor @Setter @Getter @Builder @Table(name = "file_entity") public class FileEntity implements EntityModelRelation<FileModel>
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(value = AccessLevel.NONE) private Long id;
    private String fileName;
    private Long authorId;
    private String filePath;
    @ElementCollection @CollectionTable(name = "file_user_privileges", joinColumns = @JoinColumn(name = "file_id")) private Set<String> privilege;
    @ElementCollection @CollectionTable(name = "file_entity_tags", joinColumns = @JoinColumn(name = "file_entity_id")) private Set<String> tags;

    @Override
    public FileModel toModel()
    {
        return new FileModel(id, fileName, authorId, filePath, privilege.toArray(String[]::new), tags.toArray(String[]::new));
    }
}
