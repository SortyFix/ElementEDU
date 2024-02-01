package de.gaz.eedu.user.verfication.twofa;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.user.verfication.AuthorizeService;
import de.gaz.eedu.user.verfication.JwtTokenType;
import de.gaz.eedu.user.verfication.twofa.implementations.TwoFactorMethod;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorCreateModel;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorModel;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * This is a Rest Controller class named {@code TwoFactorController} which extends {@link EntityController}
 * providing endpoints related to Two-Factor Authentication(2FA) for user login. It maps to the
 * path "/user/login/twofactor" for all its endpoint methods.
 * Class has a constructor which auto wires the {@link TwoFactorService} from
 * Spring's container and delegates the service instance to its superclass {@link EntityController}
 * The various methods in this controller allow operations related to 2FA like creating 2FA details,
 * enabling 2FA for a specific method, verifying a provided 2FA code and selecting a 2FA method.
 *
 * @author Ivo
 * @see org.springframework.stereotype.Controller
 * @see org.springframework.web.bind.annotation.RestController
 * @see org.springframework.web.bind.annotation.RequestMapping
 * @see EntityController
 * @see TwoFactorService
 * @see TwoFactorModel
 * @see TwoFactorCreateModel
 */
@RestController @RequestMapping("/user/login/twofactor") public class TwoFactorController extends EntityController<TwoFactorService, TwoFactorModel, TwoFactorCreateModel>
{

    /**
     * This is the constructor of the {@code TwoFactorController}. It uses the {@link Autowired}
     * annotation to request the dependency injection of the {@link TwoFactorService} instance.
     * The {@link TwoFactorService} instance is then passed to a constructor of the parent class {@link EntityController}.
     * The related dependencies are injected by spring's IOC container during runtime startup.
     *
     * @param entityService TwoFactorService instance
     */
    public TwoFactorController(@Autowired TwoFactorService entityService)
    {
        super(entityService);
    }

    @PostMapping("/create") @Override public @NotNull ResponseEntity<TwoFactorModel> create(@NotNull @RequestBody TwoFactorCreateModel model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthorized(authentication, JwtTokenType.ADVANCED_AUTHORIZATION))
        {
            throw unauthorizedThrowable();
        }
        return super.create(model);
    }

    /**
     * This method verifies the provided two-factor authentication code for a specific method. It is
     * a GET API mapped to "/enable/{method}/{code}" path. The method and code are specified as
     * path variables. JWT token of type {@link JwtTokenType#ADVANCED_AUTHORIZATION} is checked for the
     * validity of the request. If authorization level is not satisfied, HTTP 401 Unauthorized
     * response is returned. If authorization is satisfied, it attempts to verify two-factor
     * authentication and returns success response accordingly.
     *
     * @param method This is the two factor method for which verification is done.
     * @param code   This is the provided verification code.
     * @param claims These are the claims associated with the JWT token.
     * @return A {@link HttpStatus} representing the state of the request.
     */
    @GetMapping("/enable/{method}/{code}")
    public @NotNull HttpStatus enable(@PathVariable @NotNull TwoFactorMethod method, @PathVariable String code, @RequestAttribute("claims") Claims claims)
    {
        JwtTokenType type = JwtTokenType.ADVANCED_AUTHORIZATION;
        boolean authorized = isAuthorized(SecurityContextHolder.getContext().getAuthentication(), type);

        if (authorized && getEntityService().enable(method, code, claims))
        {
            return HttpStatus.OK;
        }
        throw unauthorizedThrowable();
    }

    /**
     * This method takes a string code and Jwt claim as input parameters, to
     * verify a provided 2FA code. If the request JWT Token type is {@link JwtTokenType#TWO_FACTOR_PENDING}
     * and the code verification is successful, it returns a successful response. If the
     * verification fails, it returns an HTTP 401 Unauthorized response.
     *
     * @param code   This is the provided Two-Factor Authentication code.
     * @param claims These are the claims associated with the associated JWT token.
     * @return Returns ResponseEntity of type {@link String}.
     */
    @GetMapping("/verify/{code}") public @NotNull ResponseEntity<String> verify(@PathVariable @NotNull String code,
            @RequestAttribute("claims") @NotNull Claims claims)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isAuthorized(authentication, JwtTokenType.TWO_FACTOR_PENDING))
        {
            TwoFactorMethod method = TwoFactorMethod.valueOf(claims.get("method", String.class));

            return getEntityService().verify(method, code, claims)
                    .map(ResponseEntity::ok).orElseThrow(this::unauthorizedThrowable);
        }

        throw unauthorizedThrowable();
    }

    /**
     * This method implements the API mapped to the "/select/{method}" path to
     * facilitate the selection of a Two-Factor Authentication method. It checks if
     * the JWT Token type is {@link JwtTokenType#TWO_FACTOR_SELECTION}. If the check is not satisfied,
     * it returns an HTTP 401 Unauthorized response, else it returns the Two Factor method
     * selection response.
     *
     * @param method This is the provided Two-Factor Authentication method.
     * @param claims These are the claims associated with the associated JWT token.
     * @return ResponseEntity of type {@link String}.
     */
    @GetMapping("/select/{method}") public @NotNull ResponseEntity<String> select(@PathVariable @NotNull TwoFactorMethod method, @RequestAttribute("claims") Claims claims)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthorized(authentication, JwtTokenType.TWO_FACTOR_SELECTION))
        {
            throw unauthorizedThrowable();
        }
        AuthorizeService authorizeService = getEntityService().getUserService().getAuthorizeService();
        return ResponseEntity.ok().body(authorizeService.selectTwoFactor(method, claims));
    }
}
