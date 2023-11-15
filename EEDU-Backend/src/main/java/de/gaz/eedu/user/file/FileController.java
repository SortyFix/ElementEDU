package de.gaz.eedu.user.file;

import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.group.GroupService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@RestController @RequestMapping(value = "/file") public class FileController
{
    private final @NotNull UserService userService;
    private final @NotNull FileService fileService;
    private final @NotNull GroupService groupService;
    private final @NotNull String currentUsername;

    public FileController(@Autowired @NotNull UserService userService, @Autowired @NotNull FileService fileService, @Autowired @NotNull GroupService groupService)
    {
        this.userService = userService;
        this.fileService = fileService;
        this.currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        this.groupService = groupService;
    }

    // ANY DIRECTORIES/FILE PATHS IN THIS CONTROLLER ARE TEMPORARY!
    @PreAuthorize("isAuthenticated()") @PostMapping("/upload") public ResponseEntity<String> uploadFile(@NotNull MultipartFile file, @NotNull String fileName, @NotNull UserEntity author, Set<Long> permittedUsers, Set<Long> permittedGroups, Set<String> tags)
    {
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
                    // Current user will be added automatically
                    permittedUsers.add(currentUser.getId());
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

    @PreAuthorize("isAuthenticated()") @PostMapping("/delete") public ResponseEntity<Boolean> deleteFile(@NotNull Long id)
    {
        return userService.loadEntityByName(currentUsername).flatMap(userEntity -> fileService.loadEntityById(id).map(fileEntity -> {
            if (fileEntity.toModel().authorId().equals(userEntity.getId())) {
                try {
                    Path path = Paths.get(fileEntity.toModel().filePath());
                    Files.delete(path);
                    fileService.delete(id);
                    return ResponseEntity.ok(true);
                } catch (IOException ioException) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        })).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }

    @PreAuthorize("isAuthenticated()") @PostMapping("/modify/tags") public ResponseEntity<Set<String>> modifyTags(@NotNull Long id, @NotNull Set<String> newTags)
    {
        Set<String> emptySet = new HashSet<>();
        return fileService.loadEntityById(id).map(fileEntity -> userService.loadEntityByName(currentUsername).map(userEntity -> {
            // Check if currently logged-in user ID matches the author ID of the file
            if(userEntity.getId().equals(fileEntity.toModel().authorId())){
                fileEntity.setTags(newTags);
                return ResponseEntity.ok(newTags);
            }
            // Return empty sets if error occurs
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(emptySet);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emptySet))).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(emptySet));
    }
}
