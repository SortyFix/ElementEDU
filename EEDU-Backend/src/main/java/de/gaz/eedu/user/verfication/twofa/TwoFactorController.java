package de.gaz.eedu.user.verfication.twofa;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.user.verfication.JwtTokenType;
import de.gaz.eedu.user.verfication.authority.VerificationAuthority;
import de.gaz.eedu.user.verfication.model.LoginResponse;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(isAuthorized(authentication, JwtTokenType.ADVANCED_AUTHORIZATION))
        {
            return super.create(model);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/verify") public @NotNull ResponseEntity<LoginResponse> verify(@RequestBody @NotNull String code, @AuthenticationPrincipal Long userID, @RequestAttribute("claims") @NotNull Claims claims)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isAuthorized(authentication, JwtTokenType.TWO_FACTOR_PENDING))
        {
            TwoFactorMethod authMethod = TwoFactorMethod.valueOf(claims.get("method", String.class));
            TwoFactorAuthModel authModel = new TwoFactorAuthModel(code, authMethod);

            return getEntityService().verify(userID, authModel, false, claims)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/enable") public ResponseEntity<LoginResponse> enable(@RequestBody @NotNull TwoFactorAuthModel authModel,
            @AuthenticationPrincipal Long userID, @RequestAttribute("claims") Claims claims)
    {
        ResponseEntity<LoginResponse> invalidResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        if (!isAuthorized(SecurityContextHolder.getContext().getAuthentication(), JwtTokenType.ADVANCED_AUTHORIZATION))
        {
            return invalidResponse;
        }
        return getEntityService().verify(userID, authModel, true, claims).map(ResponseEntity::ok).orElse(invalidResponse);
    }

    private boolean isAuthorized(@NotNull Authentication authentication, @NotNull JwtTokenType jwtTokenType)
    {
        return isAuthorized(authentication, jwtTokenType.getAuthority().getAuthority(), VerificationAuthority.class);
    }
}
