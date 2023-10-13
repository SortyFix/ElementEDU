package de.gaz.eedu.user;

import de.gaz.eedu.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.encryption.EncryptionService;
import de.gaz.eedu.user.exception.LoginNameOccupiedException;
import de.gaz.eedu.user.exception.InsecurePasswordException;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.model.UserLoginModel;
import de.gaz.eedu.user.model.UserLoginVerificationModel;
import de.gaz.eedu.user.model.UserModel;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
 * @see UserRepository
 * @see Service
 * @see AllArgsConstructor
 * @author ivo
 */
@Service
@AllArgsConstructor @Getter(AccessLevel.PROTECTED) public class UserService implements EntityService<UserEntity, UserModel, UserCreateModel>, UserDetailsService
{

    private final UserRepository userRepository;
    private final EncryptionService encryptionService;

    @Override
    public @NotNull Optional<UserEntity> loadEntityByID(long id) {
        return userRepository.findById(id);
    }

    @Override
    public @NotNull Optional<UserEntity> loadEntityByName(@NotNull String name) {
        return userRepository.findByLoginName(name);
    }

    @Override
    public @Unmodifiable @NotNull List<UserEntity> findAllEntities() {
        return userRepository.findAll();
    }

    @Override public @NotNull UserEntity createEntity(@NotNull UserCreateModel model) throws CreationException
    {
        getUserRepository().findByLoginName(model.loginName()).map(toModel()).ifPresent(occupiedModel ->
        {
            throw new LoginNameOccupiedException(occupiedModel);
        });

        String password = model.password();
        if(!password.matches("^(?=(.*[a-z])+)(?=(.*[A-Z])+)(?=(.*[0-9])+)(?=(.*[!@#$%^&*()\\-_+.])+).{6,}$"))
        {
            throw new InsecurePasswordException();
        }

        String hashedPassword = getEncryptionService().getEncoder().encode(model.password());
        return saveEntity(model.create(hashedPassword));
    }

    public @NotNull UserEntity saveEntity(@NotNull UserEntity entity)
    {
        return getUserRepository().save(entity);
    }

    @Override
    @Contract(pure = true) @Transactional(Transactional.TxType.REQUIRED) public @NotNull Function<UserModel, UserEntity> toEntity()
    {
        return userModel -> loadEntityByID(userModel.id()).orElseThrow(NullPointerException::new);
    }

    @Override
    @Contract(pure = true)
    public @org.jetbrains.annotations.NotNull Function<UserEntity, UserModel> toModel() {
        return user -> new UserModel(user.getId(), user.getFirstName(), user.getLastName(), user.getLoginName(), user.isEnabled(), user.isLocked(), user.getGroups());
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
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

    @Transactional(Transactional.TxType.REQUIRED) public @NotNull Optional<UserLoginVerificationModel> login(@NotNull UserLoginModel userLoginModel)
    {
        return loadEntityByName(userLoginModel.loginName()).map(user ->
        {
            if (getEncryptionService().getEncoder().matches(userLoginModel.password(), user.getPassword()))
            {
                return new UserLoginVerificationModel(user.getId(), getEncryptionService().generateKey(String.valueOf(user.getId())));
            }
            return null; // Optional empty as password does not match.
        });
    }

    @Transactional(Transactional.TxType.REQUIRED) public @NotNull Optional<UsernamePasswordAuthenticationToken> validate(@NotNull String token)
    {
        return getEncryptionService().validate(token, (id) -> loadEntityByID(id).map(UserEntity::getAuthorities).orElse(new HashSet<>()));
    }
}
