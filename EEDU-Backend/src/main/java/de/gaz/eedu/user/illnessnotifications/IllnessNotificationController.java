package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.UserStatus;
import de.gaz.eedu.file.FileService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;


@RestController @RequestMapping(value = "/illness/me") public class IllnessNotificationController
{
    private final UserService userService;
    private final FileService fileService;
    private final IllnessNotificationService illnessNotificationService;

    public IllnessNotificationController(@NotNull UserService userService, @NotNull FileService fileService, @NotNull IllnessNotificationService illnessNotificationService)
    {
        this.userService = userService;
        this.fileService = fileService;
        this.illnessNotificationService = illnessNotificationService;
    }

    // TODO: Only for parent accounts
    @PreAuthorize("isAuthenticated()") @PostMapping("/excuse") public ResponseEntity<Boolean> excuseCurrentUser(@AuthenticationPrincipal Long id, @NotNull String reason)
    {
        // TODO: Add logic if current day is an exam day
        return userService.loadEntityByID(id).map(userEntity ->
        {
            userEntity.setStatus(UserStatus.EXCUSED);
            IllnessNotificationCreateModel illnessNotificationCreateModel = new IllnessNotificationCreateModel(id,
                    LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond(), reason);
            illnessNotificationService.createEntity(illnessNotificationCreateModel);
            return ResponseEntity.ok(true);
        }).orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false));
    }

    // Same here
    @PreAuthorize("isAuthenticated()") @PostMapping("/excuse/upload") public ResponseEntity<Boolean> uploadIllnessNotification(@NotNull MultipartFile file, @NotNull String fileName, @AuthenticationPrincipal Long authorId, Set<Long> permittedUsers, Set<Long> permittedGroups, Set<String> tags) throws IOException, IllegalStateException
    {
        String currentDate = LocalDate.now().toString();
        boolean uploadSuccessful = fileService.upload(file, authorId, "/illnesses/", currentDate, permittedUsers, permittedGroups, tags);
        return uploadSuccessful ? ResponseEntity.ok(true) : ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
    }


}
