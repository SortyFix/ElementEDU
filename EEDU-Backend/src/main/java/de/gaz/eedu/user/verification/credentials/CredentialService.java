package de.gaz.eedu.user.verification.credentials;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.verification.ClaimHolder;
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

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Service
@AllArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class CredentialService extends EntityService<CredentialRepository, CredentialEntity, CredentialModel, CredentialCreateModel>
{
    @Getter(AccessLevel.NONE) private final CredentialRepository credentialRepository;
    private final UserService userService;

    private static void deleteTemporary(@NotNull UserEntity user, long credentialId)
    {
        if (user.disableCredential(credentialId))
        {
            String infoMessage = "User {} has enabled a new credential. The temporary credential {} has been pruned.";
            log.info(infoMessage, user.getId(), credentialId);
            return;
        }
        String warnMessage = "User {} has obtained a temporary authentication token, but lacks the associated temporary credentials.";
        log.warn(warnMessage, user.getId());
    }

    private static @NotNull Optional<Long> getTemporary(@NotNull CredentialEntity credentialEntity, @NotNull Claims claims)
    {
        if (claims.containsKey("temporary"))
        {
            long userId = credentialEntity.getUser().getId();
            long id = Objects.hash(CredentialMethod.valueOf(claims.get("temporary", String.class)), userId);
            return Optional.of(id);
        }
        return Optional.empty();
    }

    @Override public @NotNull CredentialRepository getRepository()
    {
        return credentialRepository;
    }

    @Transactional @Override
    public @NotNull CredentialEntity createEntity(@NotNull CredentialCreateModel model) throws CreationException
    {
        UserEntity userEntity = getUserService().loadEntityByIDSafe(model.userID());

        CredentialEntity entity = new CredentialEntity(model.method(), model.temporary(), userEntity);
        CredentialEntity credentialEntity = model.toEntity(entity);

        if (getRepository().existsById(credentialEntity.getId()))
        {
            throw new OccupiedException();
        }

        credentialEntity.getMethod().getCredential().creation(credentialEntity);
        validate(userEntity.initCredential(credentialEntity), new CreationException(HttpStatus.CONFLICT));

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
                ClaimHolder<?>[] temporary = {new ClaimHolder<>("temporary", entity.getMethod())};
                System.out.println(Arrays.toString(entity.allowedMethods()));
                return getUserService().getVerificationService().requestSetupCredential(method, entity.allowedMethods(), claims, temporary);
            }

            return getUserService().getVerificationService().authorize(claims);
        };
    }

    @Transactional public boolean enable(@NotNull CredentialMethod method, @NotNull String code, @NotNull Claims claims)
    {
        long userID = claims.get("userID", Long.class);
        Predicate<CredentialEntity> beenEnabled = enabled(method, code, claims);
        return getUserService().loadEntityByIDSafe(userID).getCredentials(method).stream().anyMatch(beenEnabled);
    }

    @Transactional
    protected @NotNull Predicate<CredentialEntity> enabled(@NotNull CredentialMethod method, @NotNull String code, @NotNull Claims claims)
    {
        return credential ->
        {
            if (method.isEnablingRequired() && credential.isEnabled())
            {
                return false;
            }

            if (credential.getMethod().getCredential().verify(credential, code))
            {
                getTemporary(credential, claims).ifPresent(id -> deleteTemporary(credential.getUser(), id));

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
}
