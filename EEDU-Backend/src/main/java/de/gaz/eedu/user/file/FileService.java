package de.gaz.eedu.user.file;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

@Service public class FileService
{
    FileRepository fileRepository;

    /**
     * Checks if file is empty or contains malicious files.
     * Uses ClamAV for malware detection.
     * @param file
     * <code>MultipartFile</code>
     * @return <code>Boolean</code> <br>
     * True: File is usable <br>
     * False: File is unusable
     */
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

    /**
     * Returns a <code>{@literal Set<}{@link FileEntity}{@literal >}</code> containing all FileEntities
     * matching <code>tag</code>
     * @param tag
     * <code>String</code>
     * @return <code>{@literal Set<}{@link FileEntity}{@literal >}</code>
     */
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

    /**
     * Deletes the {@link FileEntity} that holds the given <cold>id</cold>
     * parameter value as file id.
     * @param id
     * @return boolean <br>
     * True: Successfully deleted <br>
     * False: Couldn't find {@link FileEntity} with id.
     */
    public boolean delete(@NotNull Long id){
        return fileRepository.findById(id).map(fileEntity ->
        {
            fileRepository.deleteById(id);
            return true;
        }).orElse(false);
    }
}
