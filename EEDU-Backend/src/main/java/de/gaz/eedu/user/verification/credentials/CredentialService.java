package de.gaz.eedu.user.verification.credentials;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.verification.GeneratedToken;
import de.gaz.eedu.user.verification.JwtTokenType;
import de.gaz.eedu.user.verification.TokenData;
import de.gaz.eedu.user.verification.VerificationService;
import de.gaz.eedu.user.verification.credentials.implementations.Credential;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import de.gaz.eedu.user.verification.credentials.model.CredentialCreateModel;
import de.gaz.eedu.user.verification.credentials.model.CredentialModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Service
@AllArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class CredentialService extends EntityService<Long, CredentialRepository, CredentialEntity, CredentialModel, CredentialCreateModel>
{
    @Getter(AccessLevel.NONE) private final CredentialRepository credentialRepository;
    private final UserService userService;

    @Override public @NotNull CredentialRepository getRepository()
    {
        return credentialRepository;
    }

    @Transactional @Override
    public @NotNull List<CredentialEntity> createEntity(@NotNull Set<CredentialCreateModel> model) throws CreationException
    {
        Set<UserEntity> users = new HashSet<>();
        List<CredentialEntity> credentials = saveEntity(model.stream().map(current -> {
            UserEntity userEntity = getUserService().loadEntityByIDSafe(current.userID());

            CredentialEntity entity = new CredentialEntity(current.method(), current.temporary(), userEntity);
            CredentialEntity credentialEntity = current.toEntity(entity);

            if (getRepository().existsById(credentialEntity.getId()))
            {
                throw new OccupiedException();
            }

            credentialEntity.getMethod().getCredential().creation(credentialEntity);
            validate(userEntity.initCredential(credentialEntity), new CreationException(HttpStatus.CONFLICT));
            users.add(userEntity);
            return credentialEntity;
        }).toList());

        // save users having new credentials
        getUserService().saveEntity(users);

        return credentials;
    }

    @Transactional
    public @NotNull Optional<GeneratedToken> verify(@NotNull CredentialMethod method, String code, @NotNull TokenData tokenData)
    {
        UserEntity userEntity = getUserService().loadEntityByIDSafe(tokenData.userId());
        return userEntity.getCredentials(method).stream().filter(credentialEntity ->
        {
            Credential credential = credentialEntity.getMethod().getCredential();
            return credentialEntity.isEnabled() && credential.verify(credentialEntity, code);
        }).findFirst().map(toToken(tokenData));
    }

    @Transactional @Contract(pure = true, value = "_ -> new")
    protected @NotNull Function<CredentialEntity, GeneratedToken> toToken(@NotNull TokenData tokenData)
    {
        VerificationService verificationService = getUserService().getVerificationService();
        return (entity) ->
        {
            tokenData.restrictClaim("expiry");
            if (entity.isTemporary())
            {
                tokenData.addRestrictedClaim("temporary", entity.getMethod());
                JwtTokenType type = JwtTokenType.CREDENTIAL_REQUIRED;
                if (entity.allowedMethods().length == 1)
                {
                    type = JwtTokenType.CREDENTIAL_CREATION_PENDING;
                }
                return verificationService.credentialToken(type, tokenData, entity.allowedMethods());
            }

            return verificationService.authorizeToken(tokenData);
        };
    }

    @Transactional
    public boolean enable(@NotNull CredentialMethod method, @NotNull String code, @NotNull TokenData tokenData)
    {
        UserEntity user = getUserService().loadEntityByIDSafe(tokenData.userId());
        return user.getCredentials(method).stream().anyMatch(enabled(method, code, tokenData));
    }

    @Transactional
    protected @NotNull Predicate<CredentialEntity> enabled(@NotNull CredentialMethod method, @NotNull String code, @NotNull TokenData tokenData)
    {
        return credential ->
        {
            if (method.isEnablingRequired() && credential.isEnabled())
            {
                return false;
            }

            if (credential.getMethod().getCredential().verify(credential, code))
            {
                if (tokenData.additionalClaims().containsKey("temporary"))
                {
                    long userId = credential.getUser().getId();
                    deleteTemporary(Objects.hash(tokenData.get("temporary", String.class), userId), credential);
                }

                if (!credential.isEnabled())
                {
                    credential.setEnabled(true);
                    save(credential);
                }
                return true;
            }

            return false;
        };
    }

    @Transactional
    protected void deleteTemporary(long id, @NotNull CredentialEntity credential)
    {
        if (delete(new Long[] { id }))
        {
            return;
        }

        String warnMessage = "The user {} attempted to create a new credential using a temporary one; however, it appears that the temporary credential with the id {} does not exist.";
        log.warn(warnMessage, credential.getUser().getId(), id);
    }
}
