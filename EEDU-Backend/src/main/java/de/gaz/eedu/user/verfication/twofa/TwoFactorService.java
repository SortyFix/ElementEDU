package de.gaz.eedu.user.verfication.twofa;

import com.amdelamar.jotp.OTP;
import com.amdelamar.jotp.type.Type;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorCreateModel;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Service @AllArgsConstructor @Getter(AccessLevel.PROTECTED) public class TwoFactorService implements EntityService<TwoFactorEntity, TwoFactorModel, TwoFactorCreateModel>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TwoFactorService.class);
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
        UserEntity userEntity =
                getUserService().loadEntityByID(model.userID()).orElseThrow(() -> new EntityUnknownException(model.userID()));
        TwoFactorEntity twoFactorEntity;

        try
        {
            twoFactorEntity = model.toEntity(new TwoFactorEntity(userEntity));
        }
        catch (IllegalArgumentException illegalArgumentException) {throw new CreationException(HttpStatus.BAD_REQUEST);}

        if (userEntity.enableTwoFactor(twoFactorEntity))
        {
            getTwoFactorRepository().save(twoFactorEntity);
            getUserService().save(userEntity);
            return twoFactorEntity;
        }
        throw new CreationException(HttpStatus.CONFLICT);
    }

    public boolean validate(@NotNull Long userID, @NotNull TwoFactorMethod twoFactorMethod, @NotNull String code) throws IOException, NoSuchAlgorithmException, InvalidKeyException
    {
        return switch (twoFactorMethod)
        {
            case SMS -> true;
            case TOTP -> validateTOTP(userID, code);
            case EMAIL -> true;
        };
    }

    private boolean validateTOTP(@NotNull Long userID, @NotNull String code)
    {
        try
        {
            String secret = null;
            if (secret == null)
            {
                UserEntity userEntity = getUserService().loadEntityByIDSafe(userID);
                Predicate<TwoFactorEntity> isTOTPMethod = entity -> entity.getMethod().equals(TwoFactorMethod.TOTP);
                Function<TwoFactorEntity, String> mapper = TwoFactorEntity::getData;
                Stream<TwoFactorEntity> methods = userEntity.getEnabledTwoFactorMethods().stream().filter(isTOTPMethod);

                secret = methods.findFirst().map(mapper).orElseThrow(UnsupportedOperationException::new);
            }
            String hexTime = OTP.timeInHex(System.currentTimeMillis());
            return OTP.verify(secret, hexTime, code, 6, Type.TOTP);
        }
        catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception)
        {
            LOGGER.error("An error occurred when verifying totp code.", exception);
            return false;
        }
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
        return twoFactorModel -> getTwoFactorRepository().findById(twoFactorModel.id()).orElseThrow(() -> new EntityUnknownException(twoFactorModel.id()));
    }

    @Override public @NotNull Function<TwoFactorEntity, TwoFactorModel> toModel()
    {
        return TwoFactorEntity::toModel;
    }
}
