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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


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

    /**
     * Loads Theme Entity by given theme name and replaces its UserEntity.
     * If a User Entity is already present in a Theme Entity, it will be deleted.
     * Returns the modified Theme Entity for usage in the frontend application.
     * @param id
     * @param name
     * @return ThemeEntity
     */
    @PermitAll @PostMapping("/me/{id}/theme/set") public ResponseEntity<ThemeEntity> setTheme(@PathVariable @NotNull Long id, @RequestBody String name){
        logger.info("The server has recognized an incoming theme set request.");
        try{
            // Does the following code make sense Ivo?
            ThemeEntity themeEntity = themeService.loadEntityByName(name).orElseThrow(IllegalArgumentException::new);
            themeEntity.setUserEntity(userService.loadEntityByID(id).orElseThrow(IllegalArgumentException::new));
            Optional<ThemeEntity> loadedEntityByUserEntity = themeService.loadEntityByUserEntity(userService.loadEntityByID(id).orElse(null));
            // If UserEntity is already assigned to a ThemeEntity, delete said ThemeEntity
            loadedEntityByUserEntity.ifPresent(themeEntity1 -> {
                logger.info("Found already existing theme entity in given user ID. Removing...");
                themeService.delete(themeEntity1.getId());
                logger.info("Removed.");
            });
            // Save newly created ThemeEntity
            themeService.saveEntity(themeEntity);
            return ResponseEntity.ok(themeEntity);
        }
        catch(IllegalArgumentException illegalArgumentException){
            return ResponseEntity.badRequest().body(null);
        }
    }
    @PermitAll @GetMapping("/me/{id}/theme/get") public ThemeEntity getTheme(@PathVariable @NotNull Long id){
        return themeService.loadEntityByUserEntity(userService.loadEntityByID(id).orElseThrow(IllegalArgumentException::new))
                .orElseThrow(IllegalArgumentException::new);
    }
    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/theme/create") public @NotNull ResponseEntity<ThemeEntity> createTheme(@NotNull @RequestBody ThemeCreateModel themeCreateModel){
        try {
            return ResponseEntity.ok(themeService.createEntity(themeCreateModel));
        }
        catch(NameOccupiedException nameOccupiedException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/theme/delete") public @NotNull ResponseEntity<?> deleteTheme(@NotNull @RequestBody Long themeId){
        if(themeService.delete(themeId) == false)
        {
            return ResponseEntity.ok(themeService.delete(themeId));
        }
        else{
            return ResponseEntity.badRequest().body("Not a valid ID.");
        }
    }
}
