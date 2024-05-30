package de.gaz.eedu.user.verification.credentials;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import de.gaz.eedu.user.verification.credentials.model.CredentialCreateModel;
import de.gaz.eedu.user.verification.credentials.model.CredentialModel;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
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

    @Override public @NotNull CredentialEntity createEntity(@NotNull CredentialCreateModel model) throws CreationException
    {
        UserEntity userEntity = getUserService().loadEntityByIDSafe(model.userID());
        CredentialEntity credentialEntity = populateEntity(model, userEntity);

        validate(userEntity.initCredential(credentialEntity), new CreationException(HttpStatus.CONFLICT));

        getRepository().save(credentialEntity);
        getUserService().save(userEntity);

        return credentialEntity;
    }

    public @NotNull Optional<String> verify(@NotNull CredentialMethod method, String code, @NotNull Claims claims)
    {
        long userID = claims.get("userID", Long.class);
        UserEntity userEntity = getUserService().loadEntityByIDSafe(userID);

        Function<CredentialEntity, Boolean> mapper = authentication -> verifyMapper(code).apply(authentication);
        return userEntity.getCredentials(method)
                .map(mapper)
                .filter(Boolean::booleanValue)
                .map(entity -> getUserService().getAuthorizeService().authorize(userEntity.getId(), claims));
    }

    public boolean enable(@NotNull CredentialMethod method, @NotNull String code, @NotNull Claims claims)
    {
        long userID = claims.get("userID", Long.class);
        UserEntity userEntity = getUserService().loadEntityByIDSafe(userID);
        Function<CredentialEntity, Boolean> mapper = authentication -> enableMapper(code).apply(authentication);
        return userEntity.getCredentials(method).map(mapper).orElse(false);
    }

    @NotNull
    @Contract(pure = true, value = "_ -> new")
    private Function<CredentialEntity, Boolean> verifyMapper(@NotNull String code)
    {
        return credential ->
        {
            if (!credential.isEnabled())
            {
                return false;
            }

            return credential.getMethod().getCredential().verify(credential, code);
        };
    }

    @NotNull
    @Contract(pure = true, value = "_ -> new")
    private Function<CredentialEntity, Boolean> enableMapper(@NotNull String code)
    {
        return new Function<>()
        {
            @Transactional @Override public Boolean apply(@NotNull CredentialEntity credentialEntity)
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
            }
        };
    }

    /**
     * This method populates an {@link CredentialEntity}.
     * <p>
     * This method creates a new {@link CredentialEntity} by a {@link CredentialCreateModel}.
     *
     * @param model      the two-factor model to create the {@link CredentialEntity}.
     * @param userEntity the entity associated with.
     * @return the created {@link CredentialEntity}.
     */
    @Contract(value = "_, _ -> new", pure = true) private @NotNull CredentialEntity populateEntity(@NotNull CredentialCreateModel model, @NotNull UserEntity userEntity)
    {
        long id = (userEntity.getId() + " " + model.method()).hashCode();

        if(getRepository().existsById(id))
        {
            throw new OccupiedException();
        }

        return model.toEntity(new CredentialEntity(id, userEntity));
    }
}
