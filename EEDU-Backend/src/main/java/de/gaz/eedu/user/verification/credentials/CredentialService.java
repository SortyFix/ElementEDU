package de.gaz.eedu.user.verification.credentials;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.verification.credentials.implementations.Credential;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import de.gaz.eedu.user.verification.credentials.model.CredentialCreateModel;
import de.gaz.eedu.user.verification.credentials.model.CredentialModel;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

@Service @AllArgsConstructor @Getter(AccessLevel.PROTECTED) public class CredentialService extends EntityService<CredentialRepository, CredentialEntity, CredentialModel, CredentialCreateModel>
{
    @Getter(AccessLevel.NONE)
    private final CredentialRepository credentialRepository;
    private final UserService userService;

    @Override
    public @NotNull CredentialRepository getRepository()
    {
        return credentialRepository;
    }

    @Transactional @Override public @NotNull CredentialEntity createEntity(@NotNull CredentialCreateModel model) throws CreationException
    {
        UserEntity userEntity = getUserService().loadEntityByIDSafe(model.userID());
        CredentialEntity credentialEntity = model.toEntity(new CredentialEntity(userEntity));
        credentialEntity.getMethod().getCredential().creation(credentialEntity);

        validate(userEntity.initCredential(credentialEntity), new CreationException(HttpStatus.CONFLICT));

        getRepository().save(credentialEntity);
        getUserService().save(userEntity);

        return credentialEntity;
    }

    public @NotNull Optional<String> verify(@NotNull CredentialMethod method, String code, @NotNull Claims claims)
    {
        long userID = claims.get("userID", Long.class);
        UserEntity userEntity = getUserService().loadEntityByIDSafe(userID);

        return userEntity.getCredentials(method).map(credentialEntity ->
        {
            Credential credential = credentialEntity.getMethod().getCredential();
            if (credentialEntity.isEnabled() && credential.verify(credentialEntity, code))
            {
                return getUserService().getAuthorizeService().authorize(userEntity.getId(), claims);
            }
            return null;
        });
    }

    @Transactional
    public boolean enable(@NotNull CredentialMethod method, @NotNull String code, @NotNull Claims claims)
    {
        long userID = claims.get("userID", Long.class);
        Function<CredentialEntity, Boolean> mapper = credentialEntity ->
        {
            if (credentialEntity.isEnabled())
            {
                return false;
            }

            if (credentialEntity.getMethod().getCredential().verify(credentialEntity, code))
            {
                credentialEntity.setEnabled(true);
                save(credentialEntity);
                return true;
            }
            return false;
        };
        return getUserService().loadEntityByIDSafe(userID).getCredentials(method).map(mapper).orElse(false);
    }
}
