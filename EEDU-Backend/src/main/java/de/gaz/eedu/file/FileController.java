package de.gaz.eedu.file;

import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
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
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController @RequestMapping(value = "/file") public class FileController
{
    private final @NotNull UserService userService;
    private final @NotNull FileService fileService;
    private String currentUsername;

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

    @PreAuthorize("isAuthenticated()") @PostMapping("/upload") public HttpStatus uploadFile(
            @AuthenticationPrincipal Long userId, @NotNull MultipartFile file, @RequestBody @NotNull String[] authorities, @RequestBody @NotNull String directory, @RequestBody @NotNull String[] tags)
    {
        FileEntity fileEntity = new FileCreateModel(userId,
                file.getName(),
                authorities,
                directory,
                tags).toEntity(new FileEntity());
        try
        {
            fileEntity.upload(file);
            return HttpStatus.OK;
        }
        catch (IOException e)
        {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @PreAuthorize("isAuthenticated()") @PostMapping("/delete") public HttpStatus deleteFile(
            @AuthenticationPrincipal Long user_id, @NotNull Long file_id)
    {
        UserEntity userEntity = userService.loadEntityByIDSafe(user_id);
        fileService.loadEntityByID(file_id).map(fileEntity ->
        {
            if (fileEntity.getAuthorId().equals(userEntity.getId()))
            {
                fileService.delete(file_id);
                return HttpStatus.OK;
            }
            return HttpStatus.UNAUTHORIZED;
        });
        return HttpStatus.NOT_FOUND;
    }

    @PreAuthorize("isAuthenticated()") @PostMapping("/modify/tags") public HttpStatus modifyTags(
            @AuthenticationPrincipal Long userId, @NotNull Long id, @NotNull Set<String> newTags)
    {
        FileEntity fileEntity = fileService.loadEntityByIDSafe(id);
        if (fileEntity.getId().equals(userService.loadEntityByIDSafe(userId).getId()))
        {
            fileEntity.setTags(newTags);
            return HttpStatus.OK;
        }
        return HttpStatus.UNAUTHORIZED;
    }

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/{fileId}") public ResponseEntity<ByteArrayResource> downloadFileWithID(
            @AuthenticationPrincipal Long userId, @PathVariable Long fileId)
    {
        if (fileService.loadEntityByIDSafe(fileId).hasAccess(userService.loadEntityByIDSafe(userId)))
        {
            return ResponseEntity.ok(fileService.loadResourceById(fileId));
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
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
