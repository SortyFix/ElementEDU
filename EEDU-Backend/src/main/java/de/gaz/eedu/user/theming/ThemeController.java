package de.gaz.eedu.user.theming;

import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import jakarta.annotation.security.PermitAll;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping(value = "/user", method = RequestMethod.POST)
@AllArgsConstructor
public class ThemeController
{
    @Getter(AccessLevel.PROTECTED)
    private final ThemeService themeService;
    @Getter(AccessLevel.PROTECTED)
    private final UserService userService;

    /**
     * Loads User Entity by given ID and sets its Theme Entity to the one that is holding the given name.
     * Returns the Theme Entity as a model.
     * @param id ID of the theme to set
     * @param name the name of the theme to set for the user
     * @return ThemeModel
     */
    @PermitAll @PostMapping("/me/theme/set") public ResponseEntity<ThemeModel> setTheme(@AuthenticationPrincipal Long id, @RequestBody String name){
        try
        {
            UserEntity userEntity = userService.loadEntityById(id).orElseThrow(IllegalArgumentException::new);
            ThemeEntity loadedEntity = themeService.getRepository().findByName(name).orElseThrow(IllegalArgumentException::new);
            userEntity.setThemeEntity(userService, loadedEntity);
            return ResponseEntity.ok(loadedEntity.toModel());
        }
        catch(IllegalArgumentException illegalArgumentException)
        {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Get Theme Model of user with the given <code>id</code>
     * @param id ID of the theme to output
     * @return ThemeModel
     */
    @PermitAll @GetMapping("/me/theme/get") public ResponseEntity<ThemeModel> getTheme(@AuthenticationPrincipal Long id){
        if(userService.loadEntityById(id).isPresent())
        {
            return ResponseEntity.ok(userService.loadEntityById(id).get().getThemeEntity().toModel());
        }

        ThemeCreateModel fallbackTheme = new ThemeCreateModel("fallback", new short[]{255, 255, 255}, new short[]{200, 200, 200});
        // Return fallback theme if theme cannot be loaded
        return ResponseEntity.ok(fallbackTheme.toEntity(new ThemeEntity()).toModel());
    }

    /**
     * Create theme with given <code>ThemeEntity</code> data in request body.
     * Will throw a NameOccupiedException if name is already used by another Theme Entity.
     * @param themeCreateModel template of the theme to be created
     * @return ThemeEntity
     */
    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/theme/create") public @NotNull ResponseEntity<List<ThemeEntity>> createTheme(@NotNull @RequestBody Set<ThemeCreateModel> themeCreateModel)
    {
        try
        {
            return ResponseEntity.ok(themeService.createEntity(themeCreateModel));
        }
        catch(NameOccupiedException nameOccupiedException)
        {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    // Possibly set for deletion.
    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/theme/delete") public @NotNull ResponseEntity<?> deleteTheme(@NotNull @RequestBody Long themeId)
    {
        if(!themeService.delete(themeId))
        {
            return ResponseEntity.ok(themeService.delete(themeId));
        }

        throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }
}
