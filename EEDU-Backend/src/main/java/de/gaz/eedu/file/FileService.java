package de.gaz.eedu.file;

import de.gaz.eedu.file.enums.ClearStrategy;
import de.gaz.eedu.file.exception.UnknownFileException;
import de.gaz.eedu.user.UserService;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service @RequiredArgsConstructor public class FileService
{
    private final FileRepository fileRepository;

    private final UserService userService;

    public @NotNull FileRepository getRepository(){
        return fileRepository;
    }

    public @NotNull Boolean upload(@NotNull MultipartFile file, @AuthenticationPrincipal Long authorId, @NotNull String pathPrefix, @NotNull String pathSuffix, @NotNull Set<Long> permittedUsers, @NotNull Set<Long> permittedGroups, Set<String> tags) throws IOException, IllegalStateException
    {
        ScanResult scanResult = null;
        ClamavClient client;

        try
        {
            client = new ClamavClient("localhost");
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
    public @NotNull Boolean remove(@NotNull Long id)
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

    /**
     * Clears the contents of a specified directory based on the provided {@link ClearStrategy}.
     * The method supports different clearing strategies, such as removing all files and subdirectories,
     * deleting only files, or recursively removing files within subdirectories.
     *
     * @param path      The path to the directory to be cleared.
     * @param strategy  The type of clearing operation to perform on the directory.
     * @return {@code true} if the directory contents are successfully cleared according to the specified clear type,
     *         {@code false} otherwise.
     * @throws IllegalArgumentException if the provided path is {@code null}.
     * @throws IllegalArgumentException if the provided clearType is {@code null}.
     */
    public @NotNull Boolean clearDirectory(@NotNull String path, @NotNull ClearStrategy strategy)
    {
        File directory = new File(path);
        return switch(strategy)
        {
            case EVERYTHING -> Optional.of(directory)
                    .filter(File::exists)
                    .filter(File::isDirectory)
                    .map(File::listFiles)
                    .map(files -> Arrays.stream(files).allMatch(File::delete))
                    .orElse(false);

            case DIRECT -> Optional.of(directory)
                    .filter(File::exists)
                    .filter(File::isDirectory)
                    .map(File::listFiles)
                    .map(files -> Arrays.stream(files).filter(File::isFile)
                            .allMatch(File::delete)).orElse(false);

            case FILES_ONLY -> Optional.of(directory)
                    .filter(File::exists)
                    .filter(File::isDirectory)
                    .map(File::listFiles)
                    .map(files -> Arrays.stream(files).allMatch(file ->
                    {
                        if(file.isDirectory()){
                            clearDirectory(file.getAbsolutePath(), ClearStrategy.FILES_ONLY);
                        }
                        return file.delete();
                    })).orElse(false);

            case SUBDIRECT -> Optional.of(directory)
                    .filter(File::exists)
                    .filter(File::isDirectory)
                    .map(File::listFiles)
                    .filter(files -> files.length > 0)
                    .map(files -> Arrays.stream(files).allMatch(file -> {
                        if(file.isDirectory()) return clearDirectory(file.getAbsolutePath(), ClearStrategy.FILES_ONLY);
                        return true;
                    })).orElse(false);
        };
    }

    public Boolean makeDirectory(@NotNull String path)
    {
        return new File(path).mkdir();
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
