package de.gaz.eedu.user.file;

import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@RestController @RequestMapping(value = "/file") public class FileController
{
    private final @NotNull UserService userService;

    public FileController(@Autowired @NotNull UserService userService)
    {
        this.userService = userService;
    }
    // ANY DIRECTORIES/FILE PATHS IN THIS CONTROLLER ARE TEMPORARY!
    @PreAuthorize("isAuthenticated()") @PostMapping("/upload") public ResponseEntity<String> uploadFile(@NotNull MultipartFile file, @NotNull String fileName, @NotNull UserEntity author, Set<Long> permittedUsers, Set<Long> permittedGroups, Set<String> tags)
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
            return userService.loadEntityByName(SecurityContextHolder.getContext().getAuthentication().getName()).map(currentUser ->
            {
                try
                {
                    Files.copy(file.getInputStream(), uploadPath);
                    FileCreateModel createModel = new FileCreateModel(fileName, currentUser.getId(), uploadPath.toString(), permittedUsers, permittedGroups, tags);
                    fileService.createEntity(createModel);
                    return ResponseEntity.ok("File upload successful");
                }
                catch (IOException ioException)
                {
                    return ResponseEntity.status(500).body("Internal server error");
                }
            }).orElse(ResponseEntity.status(401).body("Unauthorized"));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Could not pass file check");
    }

    @PreAuthorize("isAuthenticated()") @PostMapping("/delete") public ResponseEntity<Boolean> deleteFile(Long id) throws IOException
    {
        FileService fileService = new FileService();
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.loadEntityByName(currentUsername).map(userEntity -> {
            try{
                FileEntity fileEntity = fileService.loadEntityById(id).orElse(null);
                if (fileEntity.toModel().id().equals(userEntity.getId()))
                {
                    Path path = Paths.get(fileEntity.toModel().filePath());
                    Files.delete(path);
                    fileService.delete(id);
                    return ResponseEntity.ok(true);
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
            }
            catch(IOException ioException){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
            }
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }

    @PreAuthorize("isAuthenticated()") @PostMapping("/modify/tags") public ResponseEntity<Set<String>> modifyTags(Long id, Set<String> newTags)
    {
        FileService fileService = new FileService();
        FileEntity fileEntity = fileService.loadEntityById(id).orElse(null);
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity currentUserEntity = userService.loadEntityByName(currentUsername).orElse(null);
        if(currentUserEntity != null){
            if (fileEntity != null)
            {
                // Check if currently logged-in user ID matches the author ID of the file
                if(currentUserEntity.getId().equals(fileEntity.toModel().authorId())){
                    fileEntity.setTags(newTags);
                    return ResponseEntity.ok(newTags);
                }
                return ResponseEntity.status(401).build();
            }
        }
        return ResponseEntity.notFound().build();
    }
}
