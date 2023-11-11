package de.gaz.eedu.user.file;

import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.group.GroupEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@RestController @RequestMapping(value = "/file") public class FileController
{
    // ANY DIRECTORIES/FILE PATHS IN THIS CONTROLLER ARE TEMPORARY!
    @PreAuthorize("isAuthenticated()") @PostMapping("/upload") public ResponseEntity<String> uploadFile(@NotNull MultipartFile file, @NotNull String fileName, @NotNull UserEntity author, Set<UserEntity> permittedUsers, Set<GroupEntity> permittedGroups, Set<String> tags)
    {
        FileService fileService = new FileService();
        if (fileService.isFileValid(file))
        {
            // TEMPORARY!
            String dropPath = "/upload/files/" + author.getUsername() + "/";
            Path path = Paths.get(dropPath);
            if (!Files.exists(path))
            {
                try
                {
                    Files.createDirectories(path);
                }
                catch (IOException ioException)
                {
                    return ResponseEntity.status(500).body("Could not create new directory");
                }
            }

            Path uploadPath = Paths.get(dropPath, file.getOriginalFilename());
            try
            {
                Files.copy(file.getInputStream(), uploadPath);
                FileCreateModel createModel = new FileCreateModel(fileName, uploadPath.toString(), new HashSet<>(),
                        new HashSet<>(), tags);
                fileService.createEntity(createModel);
                return ResponseEntity.ok("File uploaded successfully");
            }
            catch (IOException ioException)
            {
                return ResponseEntity.status(500).body("Could not upload file");
            }
        }
        else
        {
            return ResponseEntity.status(406).body("File not acceptable");
        }
    }
}
