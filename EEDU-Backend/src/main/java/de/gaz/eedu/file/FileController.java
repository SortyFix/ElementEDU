package de.gaz.eedu.file;

import de.gaz.eedu.file.model.FileInfoModel;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@RestController @RequiredArgsConstructor @RequestMapping(value = "/api/v1/file") public class FileController
{
    private final @NotNull UserService userService;
    private final @NotNull FileService fileService;

    @PreAuthorize("isAuthenticated()") @PostMapping("/{id}/modify/tags") public HttpStatus modifyTags(
            @AuthenticationPrincipal Long userId, @PathVariable Long id, @NotNull @RequestBody Set<String> newTags)
    {
        FileEntity fileEntity = fileService.getRepository().getReferenceById(id);
        if (fileEntity.getId().equals(userService.loadEntityByIDSafe(userId).getId()))
        {
            fileEntity.setTags(newTags);
            return HttpStatus.OK;
        }
        return HttpStatus.UNAUTHORIZED;
    }

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/info/{fileId}") public ResponseEntity<FileModel> getFileInfoById(
            @AuthenticationPrincipal Long userId, @PathVariable Long fileId)
    {
        Function<FileEntity, Boolean> access = file ->
        {
            UserEntity userEntity = userService.loadEntityByIDSafe(userId);
            return file.hasAccess(userEntity);
        };
        if (fileService.getRepository().findById(fileId).map(access).orElse(false))
        {
            return ResponseEntity.ok(fileService.loadEntityById(fileId).toModel());
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/{fileId}") public ResponseEntity<ByteArrayResource> downloadFileWithID(
            @AuthenticationPrincipal Long userId, @PathVariable Long fileId) throws IOException
    {
        Function<FileEntity, Boolean> access = file -> file.hasAccess(userService.loadEntityByIDSafe(userId));
        if (fileService.getRepository().findById(fileId).map(access).orElse(false))
        {
            return fileService.loadResourceById(fileId, null);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/{fileId}/{index}") public ResponseEntity<ByteArrayResource> downloadFileIndexWithID(
            @AuthenticationPrincipal Long userId, @PathVariable Long fileId, @PathVariable Integer index) throws IOException
    {
        Function<FileEntity, Boolean> access = file -> file.hasAccess(userService.loadEntityByIDSafe(userId));
        if (fileService.getRepository().findById(fileId).map(access).orElse(false))
        {
            return fileService.loadResourceById(fileId, index);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/{fileId}/files") public ResponseEntity<List<FileInfoModel>> getSingleFiles(
            @AuthenticationPrincipal Long userId, @PathVariable Long fileId)
    {
        Function<FileEntity, Boolean> access = file -> file.hasAccess(userService.loadEntityByIDSafe(userId));
        if (fileService.getRepository().findById(fileId).map(access).orElse(false))
        {
            return ResponseEntity.ok(fileService.getFileInfosById(fileId));
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}
