package de.gaz.eedu.file;

import de.gaz.eedu.file.exception.UnknownFileException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


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
    public @NotNull ResponseEntity<ByteArrayResource> loadResourceById(@NotNull Long id) throws IOException
    {
        File directory = new File(getRepository().findById(id).map(FileEntity::getFilePath).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        File[] files = directory.listFiles();

        if (files == null || files.length == 0)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (files.length == 1)
        {
            ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                                                                      .filename(files[0].getName())
                                                                      .build();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(contentDisposition);

            System.out.println(files[0].getName() + " " + headers);

            ResponseEntity<ByteArrayResource> responseEntity =
                    ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(URLConnection.guessContentTypeFromName(files[0].getName())))
                            .headers(headers)
                            .body(new ByteArrayResource(Files.readAllBytes(Path.of(files[0].getAbsolutePath()))));
            System.out.println(responseEntity);
            return responseEntity;
        }

        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                                                                  .filename("zip-" + files[0].getName() + ".zip")
                                                                  .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);

        return ResponseEntity.ok()
                             .contentType(MediaType.parseMediaType("application/zip; charset=UTF-8"))
                             .headers(headers)
                             .body(zipBatch(files));
    }

    public ByteArrayResource zipBatch(File @NotNull ... files)
    {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(baos)){
            for (File file : files)
            {
                zos.putNextEntry(new ZipEntry(file.getName()));
                zos.write(Files.readAllBytes(Path.of(file.getAbsolutePath())));
                zos.closeEntry();
            }
            zos.finish();
            return new ByteArrayResource(baos.toByteArray());
        } catch(IOException exception)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public @NotNull FileEntity loadEntityById(@NotNull Long id)
    {
        return fileRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public @NotNull FileEntity createEntity(@NotNull FileCreateModel model)
    {
        FileEntity fileEntity = model.toEntity(new FileEntity());
        return getRepository().save(fileEntity);
    }
}
