package de.gaz.eedu.file;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FileRepository extends JpaRepository<FileEntity, Long>
{
    @NotNull Optional<FileEntity> findById(@NotNull Long id);
    @NotNull Set<FileEntity> findFileEntitiesByTags(Set<String> tag);
    @NotNull List<FileEntity> findFileEntitiesByAuthorId(Long id);
}
