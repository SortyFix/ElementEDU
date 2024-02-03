package de.gaz.eedu.file;

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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PreAuthorize("isAuthenticated()") @PostMapping("/upload") public HttpStatus generalUpload(@NotNull MultipartFile file, @NotNull String fileName, @AuthenticationPrincipal Long authorId, Set<Long> permittedUsers, Set<Long> permittedGroups, Set<String> tags) throws IOException, IllegalStateException
    {
        Boolean uploadSuccessful = fileService.upload(file, authorId,"/general/upload/", "/", permittedUsers, permittedGroups, tags);
        return uploadSuccessful ? HttpStatus.OK
                : HttpStatus.FORBIDDEN;
    }

    @PreAuthorize("isAuthenticated()") @PostMapping("/delete") public HttpStatus deleteFile(@NotNull Long id)
    {
        return userService.getRepository().findByLoginName(currentUsername).flatMap(userEntity -> fileService.loadEntityById(id).map(fileEntity -> {
            if (fileEntity.toModel().authorId().equals(userEntity.getId())) {
                try {
                    Path path = Paths.get(fileEntity.toModel().filePath());
                    Files.delete(path);
                    fileService.delete(id);
                    return HttpStatus.OK;
                } catch (IOException ioException) {
                    return HttpStatus.INTERNAL_SERVER_ERROR;
                }
            }
            return HttpStatus.UNAUTHORIZED;
        })).orElse(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("isAuthenticated()") @PostMapping("/modify/tags") public ResponseEntity<Set<String>> modifyTags(@NotNull Long id, @NotNull Set<String> newTags)
    {
        Set<String> emptySet = new HashSet<>();
        return fileService.loadEntityById(id).map(fileEntity -> userService.getRepository().findByLoginName(currentUsername).map(userEntity -> {
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
        return fileService.loadEntityById(fileIdS).map(fileEntity -> userService.getRepository().findByLoginName(currentUsername).map(userEntity -> {
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
        Optional<Long> userId = userService.getRepository().findByLoginName(currentUsername).map(UserEntity::getId);
        return userId.map(id -> ResponseEntity.ok(fileService.loadEntitiesByAuthorId(id).stream().map(FileEntity::toModel).collect(Collectors.toList())))
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList()));
    }

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/{id}/info") public ResponseEntity<List<FileModel>> getUserFilesInfo(@PathVariable Long id){
        return userService.loadEntityByID(id).map(userEntity -> ResponseEntity.ok(fileService.loadEntitiesByAuthorId(id)
                .stream().map(FileEntity::toModel).collect(Collectors.toList())))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList()));
    }
}
