package de.gaz.eedu.user.verfication.twofa;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.verfication.twofa.implementations.TwoFactorMethod;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorCreateModel;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorModel;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.codec.binary.Base32;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.function.Function;

@Service @AllArgsConstructor @Getter(AccessLevel.PROTECTED) public class TwoFactorService extends EntityService<TwoFactorRepository, TwoFactorEntity, TwoFactorModel, TwoFactorCreateModel>
{
    private static final int BYTE_SIZE = 20;
    private static final Base32 BASE_32 = new Base32();
    @Getter(AccessLevel.NONE)
    private final TwoFactorRepository twoFactorRepository;
    private final UserService userService;

    @Override
    public @NotNull TwoFactorRepository getRepository()
    {
        return twoFactorRepository;
    }

    @Override public @NotNull TwoFactorEntity createEntity(@NotNull TwoFactorCreateModel model) throws CreationException
    {
        UserEntity userEntity = getUserService().loadEntityByIDSafe(model.userID());
        TwoFactorEntity twoFactorEntity = populateEntity(model, userEntity);

        validate(userEntity.initTwoFactor(twoFactorEntity), new CreationException(HttpStatus.CONFLICT));

        getRepository().save(twoFactorEntity);
        getUserService().save(userEntity);

        return twoFactorEntity;
    }

    public @NotNull Optional<String> verify(@NotNull TwoFactorMethod method, String code, @NotNull Claims claims)
    {
        long userID = claims.get("userID", Long.class);
        UserEntity userEntity = getUserService().loadEntityByIDSafe(userID);

        Function<TwoFactorEntity, Boolean> mapper = authentication -> verifyMapper(code).apply(authentication);
        return userEntity.getTwoFactor(method)
                .map(mapper)
                .filter(Boolean::booleanValue)
                .map(entity -> getUserService().getAuthorizeService().authorize(userEntity.getId(), claims));
    }

    public boolean enable(@NotNull TwoFactorMethod method, @NotNull String code, @NotNull Claims claims)
    {
        long userID = claims.get("userID", Long.class);
        UserEntity userEntity = getUserService().loadEntityByIDSafe(userID);
        Function<TwoFactorEntity, Boolean> mapper = authentication -> enableMapper(code).apply(authentication);
        return userEntity.getTwoFactor(method).map(mapper).orElse(false);
    }

    @NotNull
    @Contract(pure = true, value = "_ -> new")
    private Function<TwoFactorEntity, Boolean> verifyMapper(@NotNull String code)
    {
        return twoFactorEntity ->
        {
            if (!twoFactorEntity.isEnabled())
            {
                return false;
            }

            return twoFactorEntity.getMethod().getTwoFactorMethodImplementation().verify(twoFactorEntity, code);
        };
    }

    @NotNull
    @Contract(pure = true, value = "_ -> new")
    private Function<TwoFactorEntity, Boolean> enableMapper(@NotNull String code)
    {
        return new Function<>()
        {
            @Transactional @Override public Boolean apply(@NotNull TwoFactorEntity twoFactorEntity)
            {
                if (twoFactorEntity.isEnabled())
                {
                    return false;
                }

                if (twoFactorEntity.getMethod().getTwoFactorMethodImplementation().verify(twoFactorEntity, code))
                {
                    twoFactorEntity.setEnabled(true);
                    save(twoFactorEntity);
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * This method populates an {@link TwoFactorEntity}.
     * <p>
     * This method creates a new {@link TwoFactorEntity} by a {@link TwoFactorCreateModel}.
     *
     * @param model      the two-factor model to create the {@link TwoFactorEntity}.
     * @param userEntity the entity associated with.
     * @return the created {@link TwoFactorEntity}.
     */
    @Contract(value = "_, _ -> new", pure = true) private @NotNull TwoFactorEntity populateEntity(@NotNull TwoFactorCreateModel model, @NotNull UserEntity userEntity)
    {
        return model.toEntity(new TwoFactorEntity(userEntity), (entity ->
        {
            entity.setSecret(generateBase32());
            return entity;
        }));
    }

    @Contract(pure = true, value = "-> new")
    public @NotNull String generateBase32()
    {
        return new String(BASE_32.encode(getRandomBytes()));
    }

    @Contract(pure = true, value = "-> new")
    private byte @NotNull [] getRandomBytes()
    {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[BYTE_SIZE];
        secureRandom.nextBytes(bytes);
        return bytes;
    }
}
