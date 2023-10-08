package de.gaz.eedu.user;

import de.gaz.eedu.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.exception.UserEmailOccupiedException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
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
@AllArgsConstructor
public class UserService implements EntityService<UserEntity, UserModel>, UserDetailsService {

    @Getter(AccessLevel.PROTECTED)
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

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

    @Override
    public @NotNull UserEntity createEntity(@NotNull UserModel model) throws CreationException {
        userRepository.findUserByEmail(model.email()).map(toModel()).ifPresent(occupiedModel ->
        {
            throw new UserEmailOccupiedException(occupiedModel);
        });

        return saveEntity(toEntity().apply(model));
    }

    public @NotNull UserEntity saveEntity(@NotNull UserEntity entity)
    {
        return getUserRepository().save(entity);
    }

    @Override
    @Contract(pure = true)
    public @NotNull Function<UserModel, UserEntity> toEntity() {
        return userModel -> new UserEntity(userModel.id(), userModel.firstName(), userModel.lastName(), userModel.loginName(), userModel.email(), passwordEncoder.encode(userModel.password()), userModel.enabled(), userModel.locked(), userModel.groupEntities());
    }

    @Override
    @Contract(pure = true)
    public @org.jetbrains.annotations.NotNull Function<UserEntity, UserModel> toModel() {
        return user -> new UserModel(user.getId(), user.getFirstName(), user.getLastName(), user.getLoginName(), user.getEmail(), null /* safy safy yes yes */, user.isEnabled(), user.isLocked(), user.getGroups());
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadEntityByName(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Transactional(Transactional.TxType.REQUIRED) public @NotNull Optional<String> login(@NotNull UserLoginRequest userLoginRequest)
    {
        return loadEntityByName(userLoginRequest.loginName()).map(user ->
        {
            if(passwordEncoder.matches(userLoginRequest.password(), user.getPassword()))
            {
                Key key = Keys.hmacShaKeyFor(generateSecret());
                return Jwts.builder().subject(user.getLoginName())
                        .issuedAt(new Date())
                        .signWith(key).compact();
            }
            return null; // Optional empty as password does not match.
        });
    }

    private byte @NotNull [] generateSecret()
    {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        return key;
    }

    /**
     * Deletes a {@link UserEntity} from the {@link #userRepository}.
     * <p>
     * With this method a user can be fully deleted from the system.
     * Note that this is final and can not be undone. So use with caution (yonas).
     * <p>
     * The id can be received using the {@link UserEntity#getId()} or {@link UserModel#id()} method.
     *
     * @param id the id of the user to delete.
     * @return whether a user was found which could be deleted.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean delete(@NotNull Long id) {
        return userRepository.findById(id).map(userEntity -> {
            userRepository.deleteById(id);
            return true;
        }).orElse(false);
    }
}
