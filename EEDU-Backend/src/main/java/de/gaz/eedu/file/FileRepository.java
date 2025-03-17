package de.gaz.eedu.file;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long>
{
    @NotNull Optional<FileEntity> findById(@NotNull Long id);
}
