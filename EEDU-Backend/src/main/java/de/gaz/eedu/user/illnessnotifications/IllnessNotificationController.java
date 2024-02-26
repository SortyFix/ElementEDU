package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.file.FileCreateModel;
import de.gaz.eedu.file.FileEntity;
import de.gaz.eedu.file.FileService;
import de.gaz.eedu.user.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;

@RequiredArgsConstructor
@RestController @RequestMapping(value = "/illness/me") public class IllnessNotificationController
{
    private final UserService userService;
    private final FileService fileService;
    private final IllnessNotificationService illnessNotificationService;

    // TODO: Only for parent accounts
    @PreAuthorize("isAuthenticated()") @PostMapping("/excuse") public ResponseEntity<Boolean> excuseCurrentUser(
            @AuthenticationPrincipal Long id, @NotNull String reason, @NotNull Long expirationTime, @NotNull MultipartFile file)
    {
        // TODO: Add logic if current day is an exam day
        return userService.loadEntityById(id).map(userEntity ->
        {
            FileEntity fileEntity = fileService.createEntity(new FileCreateModel(id,
                    file.getName(),
                    new String[] { "Management" },
                    "illness_notifications",
                    new String[] { "illness_notification" }));
            try
            {
                fileEntity.uploadBatch(file);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            illnessNotificationService.createEntity(new IllnessNotificationCreateModel(id,
                    reason,
                    LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond(),
                    expirationTime,
                    fileEntity.getId()));
            return ResponseEntity.ok(true);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
