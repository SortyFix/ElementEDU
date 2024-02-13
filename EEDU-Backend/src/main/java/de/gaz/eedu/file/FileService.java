package de.gaz.eedu.file;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.file.exception.UnknownFileException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service @RequiredArgsConstructor public class FileService implements EntityService<FileRepository, FileEntity, FileModel, FileCreateModel>
{
    private final FileRepository fileRepository;

    private final UserService userService;

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
     * based on the provided unique identifier.
     *
     * @param id The unique identifier of the file entity to be removed.
     * @return {@code true} if the file entity is successfully removed along with its associated file,
     *         {@code false} if the entity with the given identifier is not found.
     * @throws IllegalArgumentException if the provided id is {@code null}.
     * @throws UnknownFileException if the path of a file entity is faulty and the file cannot be found
     *                              on the file system.
     */
    @Override
    @Transactional
    public boolean delete(long id)
    {
        return loadEntityByID(id).map(fileEntity -> {
            try
            {
                getRepository().deleteById(fileEntity.getId());
                return Files.deleteIfExists(Paths.get(fileEntity.getFilePath()));
            }
            catch (IOException e)
            {
                throw new UnknownFileException(id);
            }
        }).orElse(false);
    }

    /**
     * Recursively deletes a list of files and directories.
     * <p>
     * This method attempts to delete each file and directory in the provided list.
     *      * If any deletion operation fails, false will be returned. If all deletions
     *      * are successful, the method returns true.
     * </p>
     *
     * @param files A list of java.io.File objects representing the files and directories
     *              to be deleted recursively.
     * @return true if all files and directories were successfully deleted; false otherwise.
     * @throws NullPointerException if the provided list of files is null.
     * @throws SecurityException    If a security manager exists and denies delete access to any of the files.
     * @see File#delete()
     */
    public boolean deleteRecursively(@NotNull List<File> files)
    {
        return files.stream().allMatch(File::delete);
    }

    public boolean verifyAccess(@NotNull FileEntity fileEntity, @NotNull Long userId)
    {
        UserEntity userEntity = userService.loadEntityByIDSafe(userId);
        return userService.loadEntityByIDSafe(userId).getId().equals(fileEntity.toModel().authorId())
                || fileEntity.getPrivilege().stream().anyMatch(filePrivilege ->
                        userEntity.getGroups().stream()
                .flatMap(groupEntity -> groupEntity.getPrivileges().stream()).anyMatch(userPrivilege -> userPrivilege.getName().equals(filePrivilege))
        );
    }

    @Transactional
    public @NotNull ByteArrayResource loadResourceById(@NotNull Long id)
    {
        try
        {
            return new ByteArrayResource(Files.readAllBytes(Path.of(String.valueOf(loadEntityByID(id).map(FileEntity::getFilePath)))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public @NotNull List<FileEntity> loadEntitiesByAuthorId(@NotNull Long id)
    {
        return fileRepository.findFileEntitiesByAuthorId(id);
    }

    @Override @Transactional
    public @NotNull FileEntity createEntity(@NotNull FileCreateModel model)
    {
        return saveEntity(model.toEntity(new FileEntity()));
    }
}
