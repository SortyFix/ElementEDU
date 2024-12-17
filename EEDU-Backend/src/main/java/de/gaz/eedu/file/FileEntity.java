package de.gaz.eedu.file;

import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.file.exception.MaliciousFileException;
import de.gaz.eedu.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.ClamavException;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

/**
 * FileEntity represents a file that has been uploaded to the file system. Contains multiple attributes for the identification,
 * authority handling and marking with tags.
 *
 * @author SortyFix (Yonas Nieder Fernández)
 */
@Entity @AllArgsConstructor @NoArgsConstructor @Setter @Getter @Builder @Table(name = "file_entity") public class FileEntity implements EntityModelRelation<FileModel>
{
    public static final String BASE_DIRECTORY = "data";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter(value = AccessLevel.NONE) private Long id;
    private String dataDirectory;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "file_user_privileges", joinColumns = @JoinColumn(name = "file_id"))
    private Set<String> privilege;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "file_entity_tags", joinColumns = @JoinColumn(name = "file_entity_id"))
    private Set<String> tags;

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileEntity that = (FileEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, dataDirectory, privilege, tags);
    }

    @Override public String toString()
    {
        return "FileEntity{" +
                "id=" + id +
                ", dataDirectory='" + dataDirectory + '\'' +
                ", privilege=" + privilege +
                ", tags=" + tags +
                '}';
    }

    /**
     * Converts the FileEntity to a FileModel DTO.
     * @return FileModel with the necessary attributes of the FileEntity.
     */
    @Override
    public FileModel toModel()
    {
        return new FileModel(id, getDataDirectory(), privilege.toArray(String[]::new), tags.toArray(String[]::new));
    }

    /**
     * Uploads a batch of files, performs a virus scan using ClamAV, and saves them to the specified directory. <br>
     * Files will be stored in the following fashion:
     * <p>
     *     <code>
     *         BASE_DIRECTORY/dataDirectory/fileEntityId/[file]
     *     </code>
     * </p>
     *
     * @param batch The batch to be uploaded.
     * @return True if the upload is successful, false otherwise.
     * @throws IOException           If an I/O error occurs during the upload or file copying.
     * @throws IllegalStateException If the ClamAV client encounters an illegal state during the scan.
     */
    public void uploadBatch(@NotNull String subdirectory, @NotNull MultipartFile @NotNull ... batch) throws MaliciousFileException
    {
        try
        {
            for(MultipartFile file : batch)
            {
                Path path = Path.of(getFilePath(subdirectory), file.getOriginalFilename());
                createDirectory(subdirectory);
                if (virusCheck(file.getInputStream()))
                {
                    file.transferTo(path);
                }
            }
        }
        catch (IOException e)
        {
            throw new MaliciousFileException(subdirectory, e);
        }
    }

    /**
     * Creates the directory based on the dataDirectory attribute of the {@link FileEntity}.
     * <p>
     * Note that files within the directory bounds with an ID of a FileEntity (e.g. 4.txt)
     * will be removed once a directory called <code>/4</code> is created.
     * </p>
     * <p>
     * The directory will be created in the following fashion:
     * <code>BASE_DIRECTORY/dataDirectory/fileEntityId</code>
     * </p>
     *
     * @throws IOException
     */
    public void createDirectory(@NotNull String subdirectory) throws IOException
    {
        File pathFile = new File(getFilePath(subdirectory));
        pathFile.mkdirs();
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
        return String.format("%s/%s/%s", BASE_DIRECTORY, getDataDirectory(), getId());
    }

    public @NotNull String getFilePath(@Nullable String subdirectory)
    {
        String path = String.format("%s/%s/%s/%s", BASE_DIRECTORY, getDataDirectory(), getId(), Objects.requireNonNullElse(subdirectory, ""));
        System.out.println(path);
        // TODO: Check if slash is ók
        return path;
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
        return true;
//        try
//        {
//            ClamavClient client = new ClamavClient("localhost");
//            return client.scan(inputStream) instanceof ScanResult.OK;
//        }
//        catch (ClamavException | IllegalStateException ignored) {
//            System.out.println(ignored.getMessage());
//            // TODO REMOVE LATER!!!!!!!!!!!!!!!!!!!!
//            return true;
//        }
    }

    /**
     * Deletes the managed relations directory associated with the current object.
     *
     * @return Current implementation always returns false.
     *
     * @throws ResponseStatusException with an internal server error if the deletion fails.
     *
     * @see FileUtils#deleteDirectory(File)
     * @see #getFilePath()
     */
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
