package de.gaz.eedu.file;

import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.file.exception.MaliciousFileException;
import de.gaz.eedu.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.ClamavException;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * FileEntity represents a file that has been uploaded to the file system. Contains multiple attributes for the identification,
 * authority handling and marking with tags.
 *
 * @author SortyFix (Yonas Nieder Fernández)
 */
@Entity @AllArgsConstructor @NoArgsConstructor @Setter @Getter @Builder @Table(name = "file_entity") public class FileEntity implements EntityModelRelation<FileModel>
{
    private static final String BASE_DIRECTORY = "data";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(value = AccessLevel.NONE) private Long id;
    private String fileName;
    private Long authorId;
    private String dataDirectory;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "file_user_privileges", joinColumns = @JoinColumn(name = "file_id"))
    private Set<String> privilege;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "file_entity_tags", joinColumns = @JoinColumn(name = "file_entity_id"))
    private Set<String> tags;

    @Override
    public FileModel toModel()
    {
        return new FileModel(id, fileName, authorId, getDataDirectory(), privilege.toArray(String[]::new), tags.toArray(String[]::new));
    }

    /**
     * Uploads a file, performs a virus scan using ClamAV, and saves it to the specified directory.
     * If the directory doesn't already exist, it will be created.
     *
     * @param file The file to be uploaded.
     * @return True if the upload is successful, false otherwise.
     * @throws IOException           If an I/O error occurs during the upload or file copying.
     * @throws IllegalStateException If the ClamAV client encounters an illegal state during the scan.
     */
    public boolean upload(@NotNull MultipartFile file) throws IOException
    {
        Path path = Paths.get(getFilePath());

        if(!Files.isDirectory(path)) new File(getFilePath()).mkdir();

        if (virusCheck(file.getInputStream()))
        {
            file.transferTo(path);
            return true;
        }

        throw new MaliciousFileException(file.getName());
    }

    /**
     * Checks if the given {@link UserEntity} has a matching authority.
     *
     * @param userEntity UserEntity to check.
     * @return True if the userEntity has a matching authority, false otherwise.
     */
    public boolean hasAccess(@NotNull UserEntity userEntity)
    {
        return userEntity.hasAnyAuthority(getPrivilege());
    }

    /**
     * Gets the file path of the file represented by the FileEntity.
     *
     * @return String containing the file path
     * @see String
     */
    public @NotNull String getFilePath()
    {
        return String.format("%s/%s/%s/", BASE_DIRECTORY, getDataDirectory(), getId());
    }

    /**
     * Checks for malware inside the uploaded file. Requires conversion to an {@link InputStream}.
     * Uses ClamAV.
     *
     * @param inputStream The InputStream required by the ClamAV client
     * @return True if file is safe, false otherwise.
     */
    private boolean virusCheck(@NotNull InputStream inputStream)
    {
        try
        {
            ClamavClient client = new ClamavClient("localhost");
            client.ping();
            return client.scan(inputStream) instanceof ScanResult.OK;
        }
        catch (ClamavException | IllegalStateException ignored) {}
        return false;
    }

    @Override
    public boolean deleteManagedRelations()
    {
        try
        {
            FileUtils.deleteDirectory(new File(getFilePath()));
        }
        catch (IOException ioException)
        {
            String errorMessage = "The server could not delete the referenced file: " + getFilePath();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, ioException);
        }
        return false;
    }
}
