package de.gaz.eedu.user.verification.credentials;

import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.user.verification.GeneratedToken;
import de.gaz.eedu.user.verification.JwtTokenType;
import de.gaz.eedu.user.verification.TokenData;
import de.gaz.eedu.user.verification.VerificationService;
import de.gaz.eedu.user.verification.authority.VerificationAuthority;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import de.gaz.eedu.user.verification.credentials.model.CredentialCreateModel;
import de.gaz.eedu.user.verification.credentials.model.CredentialModel;
import de.gaz.eedu.user.verification.credentials.model.TemporaryCredentialCreateModel;
import de.gaz.eedu.user.verification.credentials.model.UndefinedCredentialCreateModel;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
 * @see RestController
 * @see RequestMapping
 * @see EntityController
 * @see CredentialService
 * @see CredentialModel
 * @see CredentialCreateModel
 */
@RestController
@RequestMapping("/api/v1/user/login/credentials")
@RequiredArgsConstructor
public class CredentialController extends EntityController<Long, CredentialService, CredentialModel, CredentialCreateModel>
{
    @Getter(AccessLevel.PROTECTED) private final CredentialService service;

    @PreAuthorize("hasAuthority('USER_CREDENTIAL_OTHERS_DELETE') or (@verificationService.hasToken(T(de.gaz.eedu.user.verification.JwtTokenType).ADVANCED_AUTHORIZATION and #id == authentication.principal))")
    @DeleteMapping("/delete/{id}") @Override public @NotNull HttpStatus delete(@NotNull @PathVariable Long... id)
    {
        return super.delete(id);
    }

    @GetMapping("/create/select/{method}")
    @PreAuthorize("@verificationService.hasToken(T(de.gaz.eedu.user.verification.JwtTokenType).CREDENTIAL_REQUIRED)")
    public @NotNull ResponseEntity<String> selectCreate(@PathVariable CredentialMethod method, @RequestAttribute TokenData token)
    {
        token.restrictClaim("expiry");
        VerificationService service = getService().getUserService().getVerificationService();

        GeneratedToken generated = service.credentialToken(JwtTokenType.CREDENTIAL_CREATION_PENDING, token, method);
        return ResponseEntity.ok(generated.jwt());
    }

    @PreAuthorize(
            "@verificationService.hasToken(T(de.gaz.eedu.user.verification.JwtTokenType).ADVANCED_AUTHORIZATION, T(de.gaz.eedu.user.verification.JwtTokenType).CREDENTIAL_CREATION_PENDING)"
    ) @PostMapping("/create")
    public <T> @NotNull ResponseEntity<@Nullable T> create(@NotNull @RequestBody UndefinedCredentialCreateModel model, @AuthenticationPrincipal long userId)
    {
        return create(userId, model);
    }

    @PreAuthorize("hasAuthority('USER_CREDENTIAL_OTHERS_CREATE')") @PostMapping("/create/{userId}")
    public <T> @NotNull ResponseEntity<@Nullable T> create(@PathVariable long userId, @NotNull @RequestBody UndefinedCredentialCreateModel model)
    {
        Set<CredentialCreateModel> createModels = Set.of(new CredentialCreateModel(userId, model));
        CredentialEntity credential = getService().createEntity(createModels).getFirst();
        return ResponseEntity.ok(credential.getMethod().getCredential().getSetupData(credential));
    }

    @PreAuthorize("hasAuthority('USER_CREDENTIAL_OTHERS_CREATE_TEMPORARY')") @PostMapping("/create/temporary/{userId}")
    public <T> @NotNull ResponseEntity<@Nullable T> create(@PathVariable long userId, @NotNull @RequestBody TemporaryCredentialCreateModel model)
    {
        Set<CredentialCreateModel> createModels = Set.of(new CredentialCreateModel(userId, model));
        CredentialEntity credential = getService().createEntity(createModels).getFirst();
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
     * @param token  These are the claims associated with the JWT token.
     * @return A {@link HttpStatus} representing the state of the request.
     */
    @PreAuthorize(
            "@verificationService.hasToken(T(de.gaz.eedu.user.verification.JwtTokenType).ADVANCED_AUTHORIZATION, T(de.gaz.eedu.user.verification.JwtTokenType).CREDENTIAL_CREATION_PENDING)"
    ) @PostMapping("/enable/{method}")
    public @NotNull ResponseEntity<String> enable(@PathVariable @NotNull CredentialMethod method, @RequestBody String code, @RequestAttribute @NotNull TokenData token, @NotNull HttpServletResponse response)
    {
        validate(getService().enable(method, code, token), unauthorizedThrowable());

        // return login token after user has set up the credential
        if (hasAuthority(JwtTokenType.CREDENTIAL_CREATION_PENDING.getAuthority().getAuthority(), VerificationAuthority.class))
        {
            return authorizeToken(getService().verify(method, code, token), response);
        }
        return ResponseEntity.ok(null);
    }

    /**
     * This method takes a string code and Jwt claim as input parameters, to
     * verify a provided 2FA code. If the request JWT Token type is {@link JwtTokenType#CREDENTIAL_PENDING}
     * and the code verification is successful, it returns a successful response. If the
     * verification fails, it returns an HTTP 401 Unauthorized response.
     *
     * @param code  this is the provided Two-Factor Authentication code.
     * @param token these are the claims associated with the associated JWT token.
     * @return returns ResponseEntity of type {@link String}.
     */

    @PreAuthorize("@verificationService.hasToken(T(de.gaz.eedu.user.verification.JwtTokenType).CREDENTIAL_PENDING)")
    @PostMapping("/verify")
    public @NotNull ResponseEntity<String> verify(@NotNull @RequestBody String code, @RequestAttribute @NotNull TokenData token, @NotNull HttpServletResponse response)
    {
        //noinspection unchecked
        List<String> credentials = token.get("available", List.class);
        CredentialMethod method = CredentialMethod.valueOf(credentials.getFirst());

        return authorizeToken(getService().verify(method, code, token), response);
    }

    @PreAuthorize("@verificationService.hasToken(T(de.gaz.eedu.user.verification.JwtTokenType).CREDENTIAL_SELECTION)")
    @GetMapping("/select/{method}")
    public @NotNull ResponseEntity<String> select(@PathVariable @NotNull CredentialMethod method, @RequestAttribute @NotNull TokenData token)
    {
        token.restrictClaim("expiry");
        VerificationService verificationService = getService().getUserService().getVerificationService();
        GeneratedToken generated = verificationService.credentialToken(JwtTokenType.CREDENTIAL_REQUIRED, token, method);
        return ResponseEntity.ok(generated.jwt());
    }

    private @NotNull ResponseEntity<String> authorizeToken(@NotNull Optional<GeneratedToken> token, @NotNull HttpServletResponse response)
    {
        return token.map(jwtToken ->
        {
            if (Objects.equals(jwtToken.type(), JwtTokenType.AUTHORIZED))
            {
                response.addCookie(getService().getUserService().getVerificationService().authCookie(jwtToken));
            }
            return ResponseEntity.ok(jwtToken.jwt());
        }).orElseThrow(this::unauthorizedThrowable);
    }
}
