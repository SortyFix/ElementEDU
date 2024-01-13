package de.gaz.eedu.user.theming;

import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.user.UserEntity;
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
     * Loads User Entity by given ID and sets its Theme Entity to the one that is holding the given name.
     * Returns the Theme Entity as a model.
     * @param id
     * @param name
     * @return ThemeModel
     */
    @PermitAll @PostMapping("/me/{id}/theme/set") public ResponseEntity<ThemeModel> setTheme(@PathVariable @NotNull Long id, @RequestBody String name){
        logger.info("The server has recognized an incoming theme set request.");
        try{
            UserEntity userEntity = userService.loadEntityByID(id).orElseThrow(IllegalArgumentException::new);
            ThemeEntity loadedEntity = themeService.loadEntityByName(name).orElseThrow(IllegalArgumentException::new);
            userEntity.setThemeEntity(userService, loadedEntity);
            return ResponseEntity.ok(loadedEntity.toModel());
        }
        catch(IllegalArgumentException illegalArgumentException){
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Get Theme Model of user with the given <code>id</code>
     * @param id
     * @return ThemeModel
     */
    @PermitAll @GetMapping("/me/{id}/theme/get") public ResponseEntity<ThemeModel> getTheme(@PathVariable @NotNull Long id){
        if(userService.loadEntityByID(id).isPresent()){
            return ResponseEntity.ok(userService.loadEntityByID(id).get().getThemeEntity().toModel());
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create theme with given <code>ThemeEntity</code> data in request body.
     * Will throw a NameOccupiedException if name is already used by another Theme Entity.
     * @param themeCreateModel
     * @return ThemeEntity
     */
    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/theme/create") public @NotNull ResponseEntity<ThemeEntity> createTheme(@NotNull @RequestBody ThemeCreateModel themeCreateModel){
        try {
            return ResponseEntity.ok(themeService.createEntity(themeCreateModel));
        }
        catch(NameOccupiedException nameOccupiedException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    // Possibly set for deletion.
    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/theme/delete") public @NotNull ResponseEntity<?> deleteTheme(@NotNull @RequestBody Long themeId){
        if(!themeService.delete(themeId))
        {
            return ResponseEntity.ok(themeService.delete(themeId));
        }
        else{
            return ResponseEntity.badRequest().body("Not a valid ID.");
        }
    }
}
