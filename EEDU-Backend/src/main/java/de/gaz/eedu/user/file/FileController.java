package de.gaz.eedu.user.file;

import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.group.model.SimpleUserGroupModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController @RequestMapping(value = "/file") public class FileController
{
    private final @NotNull UserService userService;
    private final @NotNull FileService fileService;
    private @NotNull String currentUsername;

    public FileController(@Autowired @NotNull UserService userService, @Autowired @NotNull FileService fileService)
    {
        this.userService = userService;
        this.fileService = fileService;
    }

    @ModelAttribute
    public void defineUsername(){
        // Create optional from potential 'null' authentication
        Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName)
                .ifPresent(username -> this.currentUsername = username);
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
            return userService.loadEntityByName(currentUsername).map(currentUser ->
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

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/{fileIdS}") public ResponseEntity<ByteArrayResource> downloadFileWithID(@PathVariable Long fileIdS)
    {
        ByteArrayResource emptyResource = new ByteArrayResource(new byte[0]);
        return fileService.loadEntityById(fileIdS).map(fileEntity -> userService.loadEntityByName(currentUsername).map(userEntity -> {
            // Reduce groups in user entity to their ids
            Set<Long> userGroupIds = Arrays.stream(userEntity.toModel().groups()).map(SimpleUserGroupModel::id).collect(Collectors.toSet());
            if(userEntity.getId().equals(fileEntity.toModel().authorId())
                    || !Collections.disjoint(userGroupIds, fileEntity.toModel().permittedGroups())
                    || fileEntity.toModel().permittedUsers().contains(userEntity.getId()))
            {
                return ResponseEntity.ok(fileService.loadResourceById(fileIdS));
            }
            // Send back empty resources as request body
            return ResponseEntity.badRequest().body(emptyResource);
        }).orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).body(emptyResource))).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/me/info") public ResponseEntity<List<FileModel>> getCurrentUserFilesInfo(){
        Optional<Long> userId = userService.loadEntityByName(currentUsername).map(UserEntity::getId);
        return userId.map(id -> ResponseEntity.ok(fileService.loadEntitiesByAuthorId(id).stream().map(FileEntity::toModel).collect(Collectors.toList())))
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList()));
    }

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/{id}/info") public ResponseEntity<List<FileModel>> getUserFilesInfo(@PathVariable Long id){
        return userService.loadEntityByID(id).map(userEntity -> ResponseEntity.ok(fileService.loadEntitiesByAuthorId(id)
                .stream().map(FileEntity::toModel).collect(Collectors.toList())))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList()));
    }
}
