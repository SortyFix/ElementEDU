package de.gaz.eedu.user.verfication.twofa;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.user.verfication.JwtTokenType;
import de.gaz.eedu.user.verfication.authority.VerificationAuthority;
import de.gaz.eedu.user.verfication.twofa.implementations.TwoFactorMethod;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorCreateModel;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorAuthModel;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorModel;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/user/login/twofactor") public class TwoFactorController extends EntityController<TwoFactorService, TwoFactorModel, TwoFactorCreateModel>
{

    public TwoFactorController(@Autowired TwoFactorService entityService)
    {
        super(entityService);
    }

    @PostMapping("/create") @Override public @NotNull ResponseEntity<TwoFactorModel> create(@NotNull @RequestBody TwoFactorCreateModel model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(isAuthorized(authentication, JwtTokenType.ADVANCED_AUTHORIZATION))
        {
            return super.create(model);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/enable") public @NotNull ResponseEntity<String> enable(@RequestBody @NotNull TwoFactorAuthModel authModel, @RequestAttribute("claims") Claims claims)
    {
        ResponseEntity<String> invalidResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        if (!isAuthorized(SecurityContextHolder.getContext().getAuthentication(), JwtTokenType.ADVANCED_AUTHORIZATION))
        {
            return invalidResponse;
        }
        return getEntityService().verify(authModel, true, claims).map(response -> ResponseEntity.<String>ok(null)).orElse(invalidResponse);
    }

    @PostMapping("/verify") public @NotNull ResponseEntity<String> verify(@RequestBody @NotNull String code, @RequestAttribute("claims") @NotNull Claims claims)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isAuthorized(authentication, JwtTokenType.TWO_FACTOR_PENDING))
        {
            TwoFactorMethod authMethod = TwoFactorMethod.valueOf(claims.get("method", String.class));
            TwoFactorAuthModel authModel = new TwoFactorAuthModel(code, authMethod);

            return getEntityService().verify(authModel, false, claims)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/select/{twoFactorMethod}") public @NotNull ResponseEntity<String> select(@PathVariable @NotNull TwoFactorMethod twoFactorMethod, @RequestAttribute Claims claims)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!isAuthorized(authentication, JwtTokenType.TWO_FACTOR_SELECTION))
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok().body(getEntityService().getUserService().getAuthorizeService().selectTwoFactor(twoFactorMethod, claims));
    }

    /**
     * Checks whether a request has a certain authority.
     * <p>
     * This method references the {@link #isAuthorized(Authentication, String, Class)} and checks
     * if a user has a permission based on a {@link JwtTokenType}.
     *
     * @param authentication the current requests authentication
     * @param jwtTokenType the token that the authentication should have to be authorized
     * @return whether a {@link Authentication} has the given token type authority
     */
    private boolean isAuthorized(@NotNull Authentication authentication, @NotNull JwtTokenType jwtTokenType)
    {
        return isAuthorized(authentication, jwtTokenType.getAuthority().getAuthority(), VerificationAuthority.class);
    }
}
