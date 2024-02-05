package de.gaz.eedu.file;

import de.gaz.eedu.file.enums.Strategy;
import de.gaz.eedu.file.exception.UnknownDirectoryException;
import de.gaz.eedu.file.exception.UnknownFileException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.group.model.SimpleUserGroupModel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.ClamavException;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service @RequiredArgsConstructor public class FileService
{
    private final FileRepository fileRepository;

    private final UserService userService;

    public @NotNull FileRepository getRepository(){
        return fileRepository;
    }

    /**
     * Uploads a file, performs a virus scan using ClamAV, and saves it to the specified directory.
     *
     * @param file            The file to be uploaded.
     * @param authorId        The ID of the author/user initiating the upload.
     * @param pathPrefix      The prefix for the path where the file will be stored.
     * @param pathSuffix      The suffix for the path where the file will be stored.
     * @param permittedUsers  The set of user IDs permitted to access the uploaded file.
     * @param permittedGroups The set of group IDs permitted to access the uploaded file.
     * @param tags            The set of tags associated with the uploaded file.
     * @return True if the upload is successful, false otherwise.
     * @throws IOException        If an I/O error occurs during the upload or file copying.
     * @throws IllegalStateException If the ClamAV client encounters an illegal state during the scan.
     */
    public boolean upload(@NotNull MultipartFile file, @AuthenticationPrincipal Long authorId, @NotNull String pathPrefix, @NotNull String pathSuffix, Set<Long> permittedUsers, Set<Long> permittedGroups, Set<String> tags) throws IOException, IllegalStateException
    {
        ScanResult scanResult = null;

        try
        {
            ClamavClient client = new ClamavClient("localhost");
            client.ping();
            scanResult = client.scan(file.getInputStream());
        }
        catch (ClamavException | IllegalStateException ignored) { }

        if (scanResult instanceof ScanResult.OK)
        {
            String dropPath = pathPrefix + userService.loadEntityByIDSafe(authorId).getFullName() + "/" + pathSuffix;
            Path path = Paths.get(dropPath);
            if (!Files.exists(path))
            {
                try
                {
                    Files.createDirectories(path);
                }
                catch (IOException ioException)
                {
                    return false;
                }
            }

            final String originalFileName = file.getOriginalFilename();
            assert originalFileName != null;
            Path uploadPath = Paths.get(dropPath, originalFileName);
            return userService.loadEntityByID(authorId).map(currentUser ->
            {
                try
                {
                    Files.copy(file.getInputStream(), uploadPath);
                    // Current user will be added automatically
                    permittedUsers.add(currentUser.getId());
                    FileCreateModel createModel = new FileCreateModel(currentUser.getId(), uploadPath.toString(), permittedUsers, permittedGroups, tags);
                    createEntity(createModel, file);
                    return true;
                }
                catch (IOException ioException)
                {
                    return false;
                }
            }).orElse(false);
        }

        return false;
    }

    /**
     * Applies a specified strategy to retrieve a list of File objects based on the provided path.
     *
     * @param strategy The strategy to be applied for file retrieval. Available strategies: <br>
     *                 - {@link Strategy#EVERYTHING}: Retrieves all files in the specified directory including subdirectories and files in subdirectories. <br>
     *                 - {@link Strategy#DIRECT}: Retrieves files directly in the specified directory. <br>
     *                 - {@link Strategy#FILES_ONLY}: Retrieves all files, including those in subdirectories, but excludes subdirectories themselves. <br>
     *                 - {@link Strategy#SUBDIRECT}: Retrieves files in subdirectories, excluding files in the specified directory.
     * @param path     The path of the directory from which files are to be retrieved.
     * @return An Optional containing the array of File objects based on the specified strategy.
     *         If the strategy is invalid or an exception occurs during the process, an empty Optional is returned.
     * @throws UnknownDirectoryException if the specified directory is unknown or inaccessible.
     *                                 This exception is thrown in case of directory-related issues.
     *                                 The message provides details about the problematic directory.
     *                                 This exception wraps any underlying exceptions that might occur during the operation.
     *                                 For example, if there are issues with file I/O or directory traversal.
     *                                 It indicates that the directory at the specified path is not accessible or does not exist.
     */
    public @NotNull List<File> strategize(@NotNull Strategy strategy, @NotNull String path)
    {
        File directory = new File(path);
        final Path start = Paths.get(path);

        return switch(strategy)
        {
            case EVERYTHING -> Arrays.asList(Optional.of(directory)
                    .filter(File::exists)
                    .filter(File::isDirectory)
                    .map(File::listFiles)
                    .orElseThrow(() -> new UnknownDirectoryException(path)));

            case DIRECT -> Arrays.asList(Optional.of(directory)
                    .filter(File::exists)
                    .filter(File::isDirectory)
                    .map(File::listFiles)
                    .map(files -> Arrays.stream(files).filter(File::isFile)
                            .toArray(File[]::new)).orElse(new File[0]));

            case FILES_ONLY -> {
                try (Stream<Path> pathStream = Files.walk(start, Integer.MAX_VALUE, FileVisitOption.FOLLOW_LINKS))
                {
                    yield Arrays.asList(Optional.of(pathStream
                                    .toList()
                                    .stream().filter(mpath -> !Files.isDirectory(mpath))
                                    .map(Path::toFile)
                                    .toArray(File[]::new))
                            .orElse(new File[0]));
                }
                catch(Exception e)
                {
                    throw new UnknownDirectoryException(path);
                }
            }

            case SUBDIRECT -> {
                try(Stream<Path> pathStream = Files.walk(start, Integer.MAX_VALUE, FileVisitOption.FOLLOW_LINKS))
                {
                    yield Arrays.asList(Optional.of(pathStream
                                    .filter(mpath -> !mpath.equals(start))
                                    .filter(mpath -> !Files.isDirectory(mpath))
                                    .map(Path::toFile)
                                    .toArray(File[]::new))
                                    .orElse(new File[0]));
                }
                catch(Exception e)
                {
                    throw new UnknownDirectoryException(path);
                }
            }
        };
    }

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
    public boolean remove(@NotNull Long id)
    {
        return loadEntityById(id).map(fileEntity -> {
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

    public boolean deleteRecursively(@NotNull List<File> files)
    {
        return files.stream().allMatch(File::delete);
    }

    public boolean moveRecursively(@NotNull List<File> files, @NotNull String targetPath)
    {
        return files.stream().allMatch(file -> {
            try
            {
                Files.move(file.toPath(), Paths.get(targetPath, file.getName()));
                return true;
            }
            catch (IOException e)
            {
                return false;
            }
        });
    }

    public boolean copyRecursively(@NotNull List<File> files, @NotNull String targetPath)
    {
        return files.stream().allMatch(file -> {
            try
            {
                Files.copy(file.toPath(), Paths.get(targetPath, file.getName()));
                return true;
            }
            catch (IOException e)
            {
                return false;
            }
        });
    }

    public boolean makeDirectory(@NotNull String path)
    {
        return new File(path).mkdir();
    }

    public boolean verifyAccess(@NotNull FileEntity fileEntity, @NotNull Long userId)
    {
        UserEntity userEntity = userService.loadEntityByIDSafe(userId);
        Set<Long> userGroupIds = Arrays.stream(userEntity.toModel().groups()).map(SimpleUserGroupModel::id).collect(Collectors.toSet());
        return userService.loadEntityByIDSafe(userId).getId().equals(fileEntity.toModel().authorId())
                || !Collections.disjoint(userGroupIds, fileEntity.toModel().permittedGroups())
                || fileEntity.toModel().permittedUsers().contains(userEntity.getId());
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
                    fileEntity -> fileEntity.toModel().filePath())))));
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
        return fileRepository.findFileEntitiesByAuthorId(id);
    }

    public @NotNull FileEntity createEntity(@NotNull FileCreateModel model, @NotNull MultipartFile file)
    {
        return fileRepository.save(model.toEntity(new FileEntity(), obj -> {
            obj.setFileName(file.getOriginalFilename());
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
