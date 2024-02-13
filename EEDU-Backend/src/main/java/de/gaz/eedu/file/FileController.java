package de.gaz.eedu.file;

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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController @RequiredArgsConstructor @RequestMapping(value = "/file") public class FileController
{
    private final @NotNull UserService userService;
    private final @NotNull FileService fileService;

    @PreAuthorize("isAuthenticated()") @PostMapping("/{id}/modify/tags") public HttpStatus modifyTags(
            @AuthenticationPrincipal Long userId, @PathVariable Long id, @NotNull Set<String> newTags)
    {
        FileEntity fileEntity = fileService.getRepository().getReferenceById(id);
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
        Function<FileEntity, Boolean> access = file -> file.hasAccess(userService.loadEntityByIDSafe(userId));
        if (fileService.getRepository().findById(fileId).map(access).orElse(false))
        {
            return ResponseEntity.ok(fileService.loadResourceById(fileId));
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/{id}/info") public ResponseEntity<List<FileModel>> getUserFilesInfo(@PathVariable Long id){
        return userService.loadEntityByID(id).map(userEntity -> ResponseEntity.ok(fileService.loadEntitiesByAuthorId(id)
                .stream().map(FileEntity::toModel).collect(Collectors.toList())))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList()));
    }
}
