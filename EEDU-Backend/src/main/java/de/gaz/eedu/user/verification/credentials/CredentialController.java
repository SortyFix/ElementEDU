package de.gaz.eedu.user.verification.credentials;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.user.verification.AuthorizeService;
import de.gaz.eedu.user.verification.JwtTokenType;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import de.gaz.eedu.user.verification.credentials.model.CredentialCreateModel;
import de.gaz.eedu.user.verification.credentials.model.CredentialModel;
import de.gaz.eedu.user.verification.credentials.model.UndefinedCredentialCreateModel;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * This is a Rest Controller class named {@code TwoFactorController} which extends {@link EntityController}
 * providing endpoints related to Two-Factor Authentication(2FA) for user login. It maps to the
 * path "/user/login/twofactor" for all its endpoint methods.
 * Class has a constructor which auto wires the {@link CredentialService} from
 * Spring's container and delegates the service instance to its superclass {@link EntityController}
 * The various methods in this controller allow operations related to 2FA like creating 2FA details,
 * enabling 2FA for a specific method, verifying a provided 2FA code and selecting a 2FA method.
 *
 * @author Ivo
 * @see org.springframework.stereotype.Controller
 * @see org.springframework.web.bind.annotation.RestController
 * @see org.springframework.web.bind.annotation.RequestMapping
 * @see EntityController
 * @see CredentialService
 * @see CredentialModel
 * @see CredentialCreateModel
 */
@RestController @RequestMapping("/user/login/credentials") @RequiredArgsConstructor public class CredentialController extends EntityController<CredentialService, CredentialModel, CredentialCreateModel>
{
    @Value("${development}") private final boolean development = false;
    @Getter(AccessLevel.PROTECTED) private final CredentialService entityService;

    @PreAuthorize("(#id == authentication.principal) && @verificationService.hasToken(T(de.gaz.eedu.user.verification.JwtTokenType).ADVANCED_AUTHORIZATION)")
    @DeleteMapping("/delete/{id}") @Override
    public @NotNull Boolean delete(@NotNull @PathVariable Long id) {return super.delete(id);}

    @GetMapping("/create/select/{method}")
    @PreAuthorize("isAuthenticated() && @verificationService.hasToken(T(de.gaz.eedu.user.verification.JwtTokenType).CREDENTIAL_REQUIRED)")
    public @NotNull ResponseEntity<String> selectCreate(@PathVariable CredentialMethod method, @RequestAttribute Claims claims)
    {
        AuthorizeService authorizeService = getEntityService().getUserService().getAuthorizeService();
        return ResponseEntity.ok(authorizeService.requestSetupCredential(method, claims));
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated() && @verificationService.hasToken(T(de.gaz.eedu.user.verification.JwtTokenType).ADVANCED_AUTHORIZATION, T(de.gaz.eedu.user.verification.JwtTokenType).CREDENTIAL_CREATION_PENDING)")
    public <T> @NotNull ResponseEntity<@Nullable T> create(@NotNull @RequestBody UndefinedCredentialCreateModel model, @NotNull @AuthenticationPrincipal Long userID)
    {
        CredentialEntity credential = getEntityService().createEntity(new CredentialCreateModel(userID, model));
        return ResponseEntity.ok(credential.getMethod().getCredential().getSetupData(credential));
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
    @PostMapping("/enable/{method}")
    @PreAuthorize("isAuthenticated() && @verificationService.hasToken(T(de.gaz.eedu.user.verification.JwtTokenType).ADVANCED_AUTHORIZATION, T(de.gaz.eedu.user.verification.JwtTokenType).CREDENTIAL_CREATION_PENDING)")
    public @NotNull ResponseEntity<String> enable(@PathVariable @NotNull CredentialMethod method, @RequestBody String code, @RequestAttribute("claims") Claims claims, @NotNull HttpServletResponse response)
    {
        validate(getEntityService().enable(method, code, claims), unauthorizedThrowable());

        if (isAuthorized(JwtTokenType.CREDENTIAL_CREATION_PENDING)) // return login token after user has setup two factor
        {
            return authorizeToken(getEntityService().verify(method, code, claims), claims, response);
        }
        return ResponseEntity.ok(null);
    }

    /**
     * This method takes a string code and Jwt claim as input parameters, to
     * verify a provided 2FA code. If the request JWT Token type is {@link JwtTokenType#CREDENTIAL_PENDING}
     * and the code verification is successful, it returns a successful response. If the
     * verification fails, it returns an HTTP 401 Unauthorized response.
     *
     * @param code   This is the provided Two-Factor Authentication code.
     * @param claims These are the claims associated with the associated JWT token.
     * @return Returns ResponseEntity of type {@link String}.
     */
    @PostMapping("/verify")
    @PreAuthorize("isAuthenticated() && @verificationService.hasToken(T(de.gaz.eedu.user.verification.JwtTokenType).CREDENTIAL_PENDING)")
    public @NotNull ResponseEntity<String> verify(@NotNull @RequestBody String code, @RequestAttribute("claims") @NotNull Claims claims, @NotNull HttpServletResponse response)
    {
        List<String> credentials = claims.get("available", List.class);
        CredentialMethod method = CredentialMethod.valueOf(credentials.getFirst());

        return authorizeToken(getEntityService().verify(method, code, claims), claims, response);
    }

    /**
     * This method implements the API mapped to the "/select/{method}" path to
     * facilitate the selection of a Two-Factor Authentication method. It checks if
     * the JWT Token type is {@link JwtTokenType#CREDENTIAL_SELECTION}. If the check is not satisfied,
     * it returns an HTTP 401 Unauthorized response, else it returns the Two Factor method
     * selection response.
     *
     * @param method This is the provided Two-Factor Authentication method.
     * @param claims These are the claims associated with the associated JWT token.
     * @return ResponseEntity of type {@link String}.
     */
    @GetMapping("/select/{method}")
    @PreAuthorize("isAuthenticated() && @verificationService.hasToken(T(de.gaz.eedu.user.verification.JwtTokenType).CREDENTIAL_SELECTION)")
    public @NotNull ResponseEntity<String> select(@PathVariable @NotNull CredentialMethod method, @RequestAttribute("claims") Claims claims)
    {
        AuthorizeService authorizeService = getEntityService().getUserService().getAuthorizeService();
        return ResponseEntity.ok(authorizeService.selectCredential(method, claims));
    }

    private @NotNull ResponseEntity<String> authorizeToken(@NotNull Optional<String> token, @NotNull Claims claims, @NotNull HttpServletResponse response)
    {
        return token.map(jwtToken ->
        {
            if (!claims.get("advanced", Boolean.class))
            {
                Cookie cookie = new Cookie("token", jwtToken);
                cookie.setPath("/");
                cookie.setDomain("localhost");
                cookie.setMaxAge(3600);
                cookie.setHttpOnly(true);
                cookie.setSecure(!development);
                response.addCookie(cookie);
            }
            return ResponseEntity.ok(jwtToken);
        }).orElseThrow(this::unauthorizedThrowable);
    }
}
