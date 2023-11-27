package de.gaz.eedu.user.illness_notifications;

import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.UserStatus;
import de.gaz.eedu.user.file.FileService;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController @RequestMapping(value = "/illness") public class IllnessNotificationController
{
    private final IllnessNotificationService illnessNotificationService;
    private final UserService userService;
    private final FileService fileService;
    private String currentUsername;

    public IllnessNotificationController(@NotNull IllnessNotificationService illnessNotificationService,
            @NotNull UserService userService, @NotNull FileService fileService)
    {
        this.illnessNotificationService = illnessNotificationService;
        this.userService = userService;
        this.fileService = fileService;
    }

    // TODO: Only for parent accounts
    @PreAuthorize("isAuthenticated()") @PostMapping("/me/excuse") public ResponseEntity<Boolean> excuseCurrentUser(@AuthenticationPrincipal Long id)
    {
        // TODO: Add logic if current day is an exam day
        return userService.loadEntityByID(id).map(userEntity ->
        {
            userEntity.setStatus(UserStatus.EXCUSED);
            return ResponseEntity.ok(true);
        }).orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false));
    }

    // Same here
    @PreAuthorize("isAuthenticated()") @PostMapping("/me/excuse/upload") public ResponseEntity<Boolean> upload(@NotNull MultipartFile file, @NotNull String fileName, @AuthenticationPrincipal Long authorId, Set<Long> permittedUsers, Set<Long> permittedGroups, Set<String> tags)
    {
        Boolean uploadSuccessful = fileService.upload(file, fileName, authorId, permittedUsers, permittedGroups, tags,true);
        return uploadSuccessful ? ResponseEntity.ok(true) : ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
    }

    // As already stated, authority still open for discussion
    @PreAuthorize("hasAuthority('ADMIN')") @GetMapping("/manage/date") public ResponseEntity<List<IllnessNotificationModel>> getNotificationsOfDay(@NotNull LocalDate date)
    {
        return ResponseEntity.ok(illnessNotificationService.loadEntitiesByDate(date).stream().map(IllnessNotificationEntity::toModel).collect(Collectors.toList()));
    }

    @PreAuthorize("hasAuthority('ADMIN')") @GetMapping("/manage/filter")
    public ResponseEntity<List<IllnessNotificationModel>> getNotificationsWithStatusOfUser(@NotNull Long userId, @NotNull IllnessNotificationStatus status){
        return ResponseEntity.ok(illnessNotificationService.loadEntitiesByUserIdWithStatus(userId, status).stream().map(IllnessNotificationEntity::toModel).collect(Collectors.toList()));
    }

    @PreAuthorize("hasAuthority('ADMIN')") @GetMapping("/manage/{date}")
    public ResponseEntity<List<IllnessNotificationModel>> getNotificationsOfDate(@NotNull @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date){
        return ResponseEntity.ok(illnessNotificationService.loadEntitiesByDate(date).stream().map(IllnessNotificationEntity::toModel).collect(Collectors.toList()));
    }

    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/manage/respond")
    public ResponseEntity<Boolean> respondToNotification(@NotNull Long notificationId, @NotNull IllnessNotificationStatus status)
    {
        return illnessNotificationService.loadEntityById(notificationId).map(illnessNotificationEntity -> {
            illnessNotificationEntity.setStatus(status);
            userService.loadEntityByID(illnessNotificationEntity.getUserId()).ifPresentOrElse(userEntity ->
                            userEntity.setStatus(status == IllnessNotificationStatus.ACCEPTED ? UserStatus.EXCUSED : UserStatus.UNEXCUSED),
                            () -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(false));
            return ResponseEntity.ok(true);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }

}
