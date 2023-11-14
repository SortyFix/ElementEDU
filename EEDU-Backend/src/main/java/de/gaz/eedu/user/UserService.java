package de.gaz.eedu.user;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.verfication.VerificationService;
import de.gaz.eedu.user.exception.InsecurePasswordException;
import de.gaz.eedu.user.exception.LoginNameOccupiedException;
import de.gaz.eedu.user.group.GroupRepository;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.model.UserLoginModel;
import de.gaz.eedu.user.verfication.model.LoginResponseModel;
import de.gaz.eedu.user.model.UserModel;
import de.gaz.eedu.user.theming.ThemeEntity;
import de.gaz.eedu.user.theming.ThemeRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class manages user related tasks.
 * <p>
 * Within this class there are method managing user related tasks such as loading users or creating them.
 * This is archived by accessing a {@link UserRepository} which is auto wired in the constructor using lombok.
 * <p>
 * The {@link Service} marks this class as a service to spring. This is necessary to use spring related features such
 * as the previously mentioned auto wiring.
 * <p>
 * Another important concept is, that all {@link UserEntity} related tasks occur here.
 * Outside the {@link UserModel} is the class to use.
 *
 * @author ivo
 * @see UserRepository
 * @see Service
 * @see AllArgsConstructor
 */
@Service @AllArgsConstructor @Getter(AccessLevel.PROTECTED) public class UserService implements EntityService<UserEntity, UserModel, UserCreateModel>, UserDetailsService
{

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ThemeRepository themeRepository;

    private final VerificationService verificationService;

    @Override public @NotNull Optional<UserEntity> loadEntityByID(long id)
    {
        return getUserRepository().findById(id);
    }

    @Override public @NotNull Optional<UserEntity> loadEntityByName(@NotNull String name)
    {
        return getUserRepository().findByLoginName(name);
    }

    @Override public @Unmodifiable @NotNull List<UserEntity> findAllEntities()
    {
        return getUserRepository().findAll();
    }

    @Transactional @Override public @NotNull UserEntity createEntity(@NotNull UserCreateModel model) throws CreationException
    {
        getUserRepository().findByLoginName(model.loginName()).map(toModel()).ifPresent(occupiedModel ->
        {
            throw new LoginNameOccupiedException(occupiedModel);
        });

        String password = model.password();
        if (!password.matches("^(?=(.*[a-z])+)(?=(.*[A-Z])+)(?=(.*[0-9])+)(?=(.*[!@#$%^&*()\\-_+.])+).{6,}$"))
        {
            throw new InsecurePasswordException();
        }

        Supplier<CreationException> exceptionSupplier = () -> new CreationException(HttpStatus.NOT_FOUND);
        ThemeEntity themeEntity = getThemeRepository().findById(model.themeId()).orElseThrow(exceptionSupplier);

        String hashedPassword = getVerificationService().getPasswordEncoder().encode(model.password());
        return saveEntity(model.toEntity(new UserEntity(), entity ->
        {
            entity.setPassword(hashedPassword); // outsource as it must be encrypted using the encryption service.
            entity.setThemeEntity(themeEntity);
            return entity;
        }));
    }

    public @NotNull UserEntity saveEntity(@NotNull UserEntity entity)
    {
        return getUserRepository().save(entity);
    }

    @Transactional @Override public @NotNull Function<UserModel, UserEntity> toEntity()
    {
        return userModel -> loadEntityByID(userModel.id()).orElseThrow(() -> new EntityUnknownException(userModel.id()));
    }

    @Override @Contract(pure = true) public @org.jetbrains.annotations.NotNull Function<UserEntity, UserModel> toModel()
    {
        return UserEntity::toModel;
    }

    @Transactional @Override public UserDetails loadUserByUsername(@NotNull String username) throws UsernameNotFoundException
    {
        return loadEntityByName(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override public boolean delete(long id)
    {
        return getUserRepository().findById(id).map(userEntity ->
        {
            getUserRepository().deleteById(id);
            return true;
        }).orElse(false);
    }

    @Transactional public @NotNull Optional<LoginResponseModel> login(@NotNull UserLoginModel userLoginModel)
    {
        return loadEntityByName(userLoginModel.loginName()).map(user ->
        {
            if (getVerificationService().getPasswordEncoder().matches(userLoginModel.password(), user.getPassword()))
            {
                ZoneId zid = ZoneId.systemDefault();

                Instant timeStamp = userLoginModel.keepLoggedIn()
                        ?  Instant.now().atZone(zid).plusWeeks(2).toInstant()
                        :  Instant.now().atZone(zid).plusDays(1).toInstant();

                if(!user.getEnabledTwoFactorMethods().isEmpty()) //two factor enabled?
                {
                    return getVerificationService().twoFactor(user.toModel(), timeStamp);
                }

                if(user.getGroups().stream().anyMatch(GroupEntity::isTwoFactorRequired)) //two factor required?
                {
                    return getVerificationService().twoFactorRequired(user.toModel(), timeStamp);
                }

                return getVerificationService().loginSuccess(user.toModel(), timeStamp);
            }
            return null; // Optional empty as password does not match.
        });
    }

    @Transactional public @NotNull Optional<UsernamePasswordAuthenticationToken> validate(@NotNull String token)
    {
        return getVerificationService().validate(token,
                (id) -> loadEntityByID(id).map(UserEntity::getAuthorities).orElse(new HashSet<>()));
    }
}
