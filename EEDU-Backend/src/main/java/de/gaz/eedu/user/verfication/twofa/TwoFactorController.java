package de.gaz.eedu.user.verfication.twofa;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.user.verfication.InternalSimpleAuthority;
import de.gaz.eedu.user.verfication.JwtTokenType;
import de.gaz.eedu.user.verfication.VerificationAuthority;
import de.gaz.eedu.user.verfication.model.LoginResponse;
import de.gaz.eedu.user.verfication.twofa.implementations.TwoFactorMethod;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorCreateModel;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorAuthModel;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorModel;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        return super.create(model);
    }

    @PostMapping("/verify") public ResponseEntity<LoginResponse> verify(@RequestBody @NotNull TwoFactorAuthModel twoFactorAuthModel, @AuthenticationPrincipal Long userID, @RequestAttribute("claims") Claims claims)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean authorized = isAuthorized(authentication, JwtTokenType.TWO_FACTOR_PENDING);
        boolean hasAuthority = authorized && authentication.getAuthorities()
                .stream()
                .filter(authority -> authority instanceof InternalSimpleAuthority)
                .map(GrantedAuthority::getAuthority)
                .anyMatch(twoFactorAuthModel.twoFactorMethod().name()::equals);

        if (hasAuthority)
        {
            return getEntityService().verify(userID, twoFactorAuthModel, false, claims)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/enable") public ResponseEntity<LoginResponse> enable(@RequestBody @NotNull TwoFactorAuthModel twoFactorAuthModel, @AuthenticationPrincipal Long userID, @RequestAttribute("claims") Claims claims)
    {
        if (isAuthorized(SecurityContextHolder.getContext().getAuthentication(), JwtTokenType.ADVANCED_AUTHORIZATION))
        {
            return getEntityService().verify(userID, twoFactorAuthModel, true, claims)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/request") public @NotNull ResponseEntity<@Nullable LoginResponse> request(@NotNull @RequestBody TwoFactorMethod twoFactorMethod, @AuthenticationPrincipal long userID)
    {
        if (!isAuthorized(SecurityContextHolder.getContext().getAuthentication(), JwtTokenType.TWO_FACTOR_SELECTION))
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return null; //TODO
    }

    private boolean isAuthorized(@NotNull Authentication authentication, @NotNull JwtTokenType jwtTokenType)
    {
        return isAuthorized(authentication, jwtTokenType.getAuthority().getAuthority(), VerificationAuthority.class);
    }
}
