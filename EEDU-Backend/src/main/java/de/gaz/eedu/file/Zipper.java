package de.gaz.eedu.file;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper
{
    public ByteArrayResource zipBatch(File... files)
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
}
