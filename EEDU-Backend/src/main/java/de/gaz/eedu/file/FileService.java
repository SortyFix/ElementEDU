package de.gaz.eedu.file;

import de.gaz.eedu.file.exception.UnknownFileException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


@Service @RequiredArgsConstructor public class FileService
{
    private final FileRepository fileRepository;

    public @NotNull FileRepository getRepository(){
        return fileRepository;
    }

    /**
     * Returns the file at the specified path as a {@link File} object.
     *
     * @param path A non-null string representing the file or directory specified.
     * @return {@link File} object representing the file or directory specified.
     * @throws NullPointerException if the path is {@code null}.
     * @see File
     */
    public @NotNull File getFileOfPath(@NotNull String path)
    {
        return new File(path);
    }

    /**
     * Removes a file entity from the repository and deletes the corresponding file on the filesystem,
     * based on the provided unique identifier. Also takes a runnable which shall cleanly clear relations.
     *
     * @param id The unique identifier of the file entity to be removed.
     * @return {@code true} if the file entity is successfully removed along with its associated file,
     *         {@code false} if the entity with the given identifier is not found.
     * @throws IllegalArgumentException if the provided id is {@code null}.
     * @throws UnknownFileException if the path of a file entity is faulty and the file cannot be found
     *                              on the file system.
     */

    @Transactional
    public boolean delete(long id, @NotNull Runnable deleteTask)
    {
        return getRepository().findById(id).map(fileEntity -> {
            try
            {
                deleteTask.run();
                getRepository().deleteById(fileEntity.getId());
                FileUtils.deleteDirectory(new File(fileEntity.getFilePath()));
                return true;
            }
            catch (IOException ioException)
            {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "", ioException);
            }
        }).orElse(false);
    }

    @Transactional
    public @NotNull ByteArrayResource loadResourceById(@NotNull Long id)
    {
        try
        {
            return new ByteArrayResource(Files.readAllBytes(Path.of(String.valueOf(getRepository().findById(id).map(FileEntity::getFilePath)))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public @NotNull List<FileEntity> loadEntitiesByAuthorId(@NotNull Long id)
    {
        return fileRepository.findFileEntitiesByAuthorId(id);
    }

    @Transactional
    public @NotNull FileEntity createEntity(@NotNull FileCreateModel model)
    {
        FileEntity fileEntity = getRepository().save(model.toEntity(new FileEntity()));

        try
        {
            if(Files.exists(Path.of(fileEntity.getFilePath())))
            {
                return fileEntity;
            }

            fileEntity.createDirectory();
        }
        catch (IOException ioException)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return fileEntity;
    }
}
