package de.gaz.eedu.user.verfication.twofa;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.verfication.twofa.implementations.TwoFactorMethod;
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

import java.util.*;
import java.util.function.Function;

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

    public <T> @NotNull T setup(@NotNull Long userID, @NotNull TwoFactorMethod twoFactorMethod)
    {
        return twoFactorMethod.getTwoFactorMethodImplementation().setup(getUserService(), userID);
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
