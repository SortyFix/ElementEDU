package de.gaz.eedu.file;

import de.gaz.eedu.user.UserEntity;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@Service @RequiredArgsConstructor public class FileService
{
    private final FileRepository fileRepository;

    private final UserService userService;

    public @NotNull Boolean upload(@NotNull MultipartFile file, @NotNull String fileName, @AuthenticationPrincipal Long authorId, Set<Long> permittedUsers, Set<Long> permittedGroups, Set<String> tags, @NotNull Boolean illnessNotification) throws IOException, IllegalStateException
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
            String date = LocalDate.now().toString();
            // TEMPORARY!
            String dropPath = "/upload/files/" + userService.loadEntityByID(authorId).map(UserEntity::getUsername) + "/" + (illnessNotification ? "missing/" + date + "/" : null);
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

            Path uploadPath = Paths.get(dropPath, file.getOriginalFilename());
            return userService.loadEntityByID(authorId).map(currentUser ->
            {
                try
                {
                    Files.copy(file.getInputStream(), uploadPath);
                    // Current user will be added automatically
                    permittedUsers.add(currentUser.getId());
                    FileCreateModel createModel = new FileCreateModel(fileName, currentUser.getId(), uploadPath.toString(), permittedUsers, permittedGroups, tags);
                    createEntity(createModel);
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
        return fileRepository.findFileEntitiesByAuthorId(id);
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
