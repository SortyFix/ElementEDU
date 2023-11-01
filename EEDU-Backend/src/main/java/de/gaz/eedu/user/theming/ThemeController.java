package de.gaz.eedu.user.theming;

import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import jakarta.annotation.security.PermitAll;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.catalina.User;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.NoSuchElementException;
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

    @PermitAll @GetMapping("/me/{id}/theme/set") public ResponseEntity<ThemeEntity> setTheme(@NotNull @PathVariable Long id, @RequestBody ThemeStyle themeStyle){
        logger.info("The server has recognized an incoming theme set request.");
        ThemeEntity themeEntity = new ThemeEntity();
        switch(themeStyle){
            case FIRE:
                if(userService.loadEntityByID(id).isPresent()){
                    themeEntity.setBackgroundColor(0xa53c26);
                    themeEntity.setWidgetColor(0xedd8d4);
                    themeEntity.setTextColor(0xFFFFFF);
                    themeEntity.setName("Fire");
                    // orElse(new UserEntity) will never be thrown.
                    themeEntity.setUserEntity(userService.loadEntityByID(id).orElse(new UserEntity()));
                    return ResponseEntity.ok(themeEntity);
                }
                else{
                    return ResponseEntity.badRequest().build();
                }
            default:
                return ResponseEntity.badRequest().build();
        }
    }
}
