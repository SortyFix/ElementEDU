package de.gaz.eedu.user.verification.credentials;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.verification.VerificationService;
import de.gaz.eedu.user.verification.credentials.implementations.Credential;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import de.gaz.eedu.user.verification.credentials.model.CredentialCreateModel;
import de.gaz.eedu.user.verification.credentials.model.CredentialModel;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class CredentialService extends EntityService<CredentialRepository, CredentialEntity, CredentialModel, CredentialCreateModel>
{
    @Getter(AccessLevel.NONE) private final CredentialRepository credentialRepository;
    private final UserService userService;

    private static void disableTemporary(@NotNull CredentialMethod method, CredentialEntity credentialEntity)
    {
        Stream<CredentialEntity> credentials = credentialEntity.getUser().getCredentials(method).stream().filter(CredentialEntity::isTemporary);
        Long[] credentialIds = credentials.map(CredentialEntity::getId).toArray(Long[]::new);
        credentialEntity.getUser().disableCredential(credentialIds);

        long userId = credentialEntity.getUser().getId();
    }

    @Override public @NotNull CredentialRepository getRepository()
    {
        return credentialRepository;
    }

    @Transactional @Override
    public @NotNull CredentialEntity createEntity(@NotNull CredentialCreateModel model) throws CreationException
    {
        UserEntity userEntity = getUserService().loadEntityByIDSafe(model.userID());
        CredentialEntity credentialEntity = model.toEntity(new CredentialEntity(userEntity));
        credentialEntity.getMethod().getCredential().creation(credentialEntity);

        validate(userEntity.initCredential(credentialEntity), new CreationException(HttpStatus.CONFLICT));

        getRepository().save(credentialEntity);
        getUserService().save(userEntity);

        return credentialEntity;
    }

    @Transactional
    public @NotNull Optional<String> verify(@NotNull CredentialMethod method, String code, @NotNull Claims claims)
    {
        long userID = claims.get("userID", Long.class);
        UserEntity userEntity = getUserService().loadEntityByIDSafe(userID);

        return userEntity.getCredentials(method).stream().filter(credentialEntity ->
        {
            Credential credential = credentialEntity.getMethod().getCredential();
            return credentialEntity.isEnabled() && credential.verify(credentialEntity, code);
        }).findFirst().map(toToken(method, claims));
    }

    @Transactional @Contract(pure = true, value = "_, _ -> new")
    protected @NotNull Function<CredentialEntity, String> toToken(@NotNull CredentialMethod method, @NotNull Claims claims)
    {
        return (entity) ->
        {
            if (entity.isTemporary())
            {
                return getUserService().getVerificationService().requestSetupCredential(entity.getId(), method, claims);
            }

            return getUserService().getVerificationService().authorize(claims);
        };
    }

    @Transactional public boolean enable(@NotNull CredentialMethod method, @NotNull String code, @NotNull Claims claims)
    {
        long userID = claims.get("userID", Long.class);
        Predicate<CredentialEntity> wasEnabled = credentialEntity ->
        {
            if (credentialEntity.isEnabled() && method.isEnablingRequired())
            {
                return false;
            }

            if (credentialEntity.getMethod().getCredential().verify(credentialEntity, code))
            {
                if (claims.containsKey("temporaryId"))
                {
                    long credentialId = claims.get("temporaryId", Long.class);
                    if (credentialEntity.getUser().disableCredential(credentialId))
                    {
                        log.info("User {} has enabled a new credential of the type {}. The temporary credential {} has been pruned.", userID, method, credentialId);
                    }
                }

                credentialEntity.setEnabled(true);
                save(credentialEntity);
                return true;
            }

            return false;
        };


        return getUserService().loadEntityByIDSafe(userID).getCredentials(method).stream().anyMatch(wasEnabled);
    }
}
