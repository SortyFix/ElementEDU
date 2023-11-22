package de.gaz.eedu.user.file;

import de.gaz.eedu.user.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public @NotNull ByteArrayResource loadResourceById(@NotNull Long id)
    {
        try
        {
            return new ByteArrayResource(Files.readAllBytes(Path.of(String.valueOf(loadEntityById(id).map(
                    fileEntity -> fileEntity.toModel().filePath()
                    )))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public @NotNull List<FileEntity> loadEntitiesByAuthorId(@NotNull Long id)
    {
        return fileRepository.findFileEntitiesByAuthorId(id).stream().filter(fileEntity ->
                Objects.equals(fileEntity.toModel().authorId(), id))
                .collect(Collectors.toList());
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
