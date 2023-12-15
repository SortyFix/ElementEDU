package de.gaz.eedu.file;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FileRepository extends JpaRepository<FileEntity, Long>
{
    @NotNull Optional<FileEntity> findById(Long id);
    @NotNull Optional<FileEntity> findByFilePath(String filePath);
    @NotNull Set<FileEntity> findFileEntitiesByTags(String tag);
    @NotNull List<FileEntity> findFileEntitiesByAuthorId(Long id);
}
