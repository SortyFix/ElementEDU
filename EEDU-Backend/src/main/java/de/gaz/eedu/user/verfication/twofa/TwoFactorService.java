package de.gaz.eedu.user.verfication.twofa;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.verfication.model.LoginResponse;
import de.gaz.eedu.user.verfication.twofa.implementations.TwoFactorMethodImplementation;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorCreateModel;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorAuthModel;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorModel;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.codec.binary.Base32;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Service @AllArgsConstructor @Getter(AccessLevel.PROTECTED) public class TwoFactorService implements EntityService<TwoFactorEntity, TwoFactorModel, TwoFactorCreateModel>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TwoFactorService.class);
    private static final int BYTE_SIZE = 20;
    private static final Base32 BASE_32 = new Base32();
    private final TwoFactorRepository twoFactorRepository;
    private final UserService userService;

    @Override public @NotNull Optional<TwoFactorEntity> loadEntityByID(long id)
    {
        return Optional.empty();
    }

    @Override public @NotNull Optional<TwoFactorEntity> loadEntityByName(@NotNull String name)
    {
        throw new UnsupportedOperationException();
    }

    @Override public @Unmodifiable @NotNull List<TwoFactorEntity> findAllEntities()
    {
        return getTwoFactorRepository().findAll();
    }

    @Override public @NotNull TwoFactorEntity createEntity(@NotNull TwoFactorCreateModel model) throws CreationException
    {
        Supplier<EntityUnknownException> exceptionSupplier = () -> new EntityUnknownException(model.userID());
        UserEntity userEntity = getUserService().loadEntityByID(model.userID()).orElseThrow(exceptionSupplier);
        TwoFactorEntity twoFactorEntity;

        try
        {
            twoFactorEntity = model.toEntity(new TwoFactorEntity(userEntity), (entity ->
            {
                entity.setSecret(generateBase32());
                return entity;
            }));


            if (userEntity.initTwoFactor(twoFactorEntity))
            {
                getTwoFactorRepository().save(twoFactorEntity);
                getUserService().save(userEntity);

                return twoFactorEntity;
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {throw new CreationException(HttpStatus.BAD_REQUEST);}

        throw new CreationException(HttpStatus.CONFLICT);
    }

    @Override public boolean delete(long id)
    {
        return getTwoFactorRepository().findById(id).map(twoFactor ->
        {
            twoFactor.getUser().disableTwoFactor(getUserService(), twoFactor.getId());
            getTwoFactorRepository().deleteById(id);
            return true;
        }).orElse(false);
    }

    @Override public @NotNull TwoFactorEntity saveEntity(@NotNull TwoFactorEntity entity)
    {
        return getTwoFactorRepository().save(entity);
    }

    @Override public @NotNull Function<TwoFactorModel, TwoFactorEntity> toEntity()
    {
        return twoFactorModel -> getTwoFactorRepository().findById(twoFactorModel.id())
                .orElseThrow(() -> new EntityUnknownException(twoFactorModel.id()));
    }

    @Override public @NotNull Function<TwoFactorEntity, TwoFactorModel> toModel()
    {
        return TwoFactorEntity::toModel;
    }

    public @NotNull Optional<LoginResponse> verify(long userID, @NotNull TwoFactorAuthModel authModel, boolean enable
            , @NotNull Claims claims)
    {
        UserEntity userEntity = getUserService().loadEntityByIDSafe(userID);

        Function<TwoFactorEntity, Boolean> mapper = auth ->
        {
            if (enable)
            {
                enableMapper(authModel).apply(auth);
            }
            return verifyMapper(authModel).apply(auth);
        };

        return userEntity.getTwoFactor(authModel.twoFactorMethod())
                .map(mapper)
                .filter(Boolean::booleanValue)
                .map(entity -> getUserService().getAuthorizeService().authorize(userEntity.toModel(), claims));
    }

    @Contract(pure = true) @NotNull private Function<TwoFactorEntity, Boolean> verifyMapper(@NotNull TwoFactorAuthModel twoFactorAuthModel)
    {
        return twoFactorEntity ->
        {
            if (!twoFactorEntity.isEnabled())
            {
                return false;
            }

            TwoFactorMethodImplementation impl = twoFactorEntity.getMethod().getTwoFactorMethodImplementation();
            return impl.verify(twoFactorEntity, twoFactorAuthModel.code());
        };
    }

    @Contract(pure = true) @NotNull private Function<TwoFactorEntity, Boolean> enableMapper(@NotNull TwoFactorAuthModel twoFactorAuthModel)
    {
        return new Function<>()
        {
            @Transactional @Override public Boolean apply(@NotNull TwoFactorEntity twoFactorEntity)
            {
                if (twoFactorEntity.isEnabled())
                {
                    return false;
                }

                TwoFactorMethodImplementation impl = twoFactorEntity.getMethod().getTwoFactorMethodImplementation();
                if (impl.verify(twoFactorEntity, twoFactorAuthModel.code()))
                {
                    twoFactorEntity.setEnabled(true);
                    save(twoFactorEntity);
                    return true;
                }
                return false;
            }
        };
    }

    public @NotNull String generateBase32()
    {
        return new String(BASE_32.encode(getRandomBytes()));
    }

    private byte @NotNull [] getRandomBytes()
    {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[BYTE_SIZE];
        secureRandom.nextBytes(bytes);
        return bytes;
    }
}
