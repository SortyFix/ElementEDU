package de.gaz.eedu.user.theming;

import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.user.UserService;
import jakarta.annotation.security.PermitAll;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/user", method = RequestMethod.POST)
@AllArgsConstructor
public class ThemeController
{
    private static final Logger logger = LoggerFactory.getLogger(ThemeController.class);
    @Getter(AccessLevel.PROTECTED)
    private final ThemeService themeService;
    @Getter(AccessLevel.PROTECTED)
    private final UserService userService;

    @PermitAll @PostMapping("/me/{id}/theme/set") public ResponseEntity<ThemeEntity> setTheme(@PathVariable @NotNull Long id, @RequestBody String name){
        logger.info("The server has recognized an incoming theme set request.");
        try{
            ThemeEntity themeEntity = themeService.loadEntityByName(name).orElseThrow(IllegalArgumentException::new);
            themeEntity.setUserEntity(userService.loadEntityByID(id).orElseThrow(IllegalArgumentException::new));
            return ResponseEntity.ok(themeEntity);
        }
        catch(IllegalArgumentException illegalArgumentException){
            return ResponseEntity.badRequest().body(null);
        }
    }
    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/theme/create") public @NotNull ResponseEntity<ThemeEntity> createTheme(@NotNull @RequestBody ThemeCreateModel themeCreateModel){
        try {
            return ResponseEntity.ok(themeService.createEntity(themeCreateModel));
        }
        catch(NameOccupiedException nameOccupiedException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }
}
