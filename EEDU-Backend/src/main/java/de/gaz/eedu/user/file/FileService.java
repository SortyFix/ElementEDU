package de.gaz.eedu.user.file;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

@Service public class FileService
{
    FileRepository fileRepository;

    public @NotNull Boolean isFileValid(@NotNull MultipartFile file)
    {
        // TODO: Implement antivirus check (with ClamAV?)
        return !file.isEmpty();
    }

    public @NotNull Optional<FileEntity> loadEntityById(@NotNull Long id)
    {
        // Return empty FileEntity if not found
        return fileRepository.findById(id);
    }

    public @NotNull Set<FileEntity> loadEntitiesByTag(@NotNull String tag)
    {
        return fileRepository.findFileEntitiesByTags(tag);
    }

    public @NotNull FileEntity createEntity(@NotNull FileCreateModel model)
    {
        return fileRepository.save(model.toEntity(new FileEntity(), obj -> {
            // TODO: Implement stuff
            return obj;
        }));
    }
}
