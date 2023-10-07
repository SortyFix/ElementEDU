package de.gaz.sp.user;

import de.gaz.sp.user.exception.UserEmailOccupiedException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Loads a {@link UserModel} by his id.
     * <p>
     * This method loads a {@link UserModel} object straight from the database.
     * This is useful when working with any user related data.
     * In this specific case the user identified by his id.
     * This id is unique, and therefore it's safe to assume there is only one user having this id.
     *
     * @param id of the user to load
     * @return an {@link Optional} containing the user when his profile has been found, otherwise {@link Optional#empty()}
     */
    public @NotNull Optional<UserModel> loadUserByID(@NotNull Long id)  {
        return userRepository.findById(id).map(toModel());
    }

    /**
     * Loads a {@link UserModel} by his name.
     * <p>
     * This method loads a {@link UserModel} object straight from the database.
     * This is useful when working with any user related data.
     * In this specific case the user is identified by his last name.
     * <p>
     * Note that this method is not safe as there could be two people with the same last name.
     * It's recommended therefore to use {@link #loadUserByID(Long)} instead.
     *
     * @param lastName of the user to load
     * @return an {@link Optional} containing the user when his profile has been found, otherwise {@link Optional#empty()}
     * @see #loadUserByID(Long)
     */
    public @NotNull Collection<UserModel> loadUserByUsername(@NotNull String lastName) {
        return userRepository.findUserByLastName(lastName).stream().map(toModel()).toList();
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
    public boolean delete(@NotNull Long id) {
        return userRepository.findById(id).map(userEntity ->
        {
            userRepository.deleteById(id);
            return true;
        }).orElse(false);
    }

    /**
     * Returns a list of all users transformed into {@link UserModel}
     * <p>
     * This method is an own implementation of the {@code findAll()} method from the {@link #userRepository}.
     * It gets all the users stored in the database and transforms them into {@link UserModel} which then can be sent to the frontend.
     *
     * @return An unmodifiable list containing all users transformed into UserModels.
     * @see UserRepository#findAll()
     */
    public @NotNull @Unmodifiable List<UserModel> findAllUsers() {
        return userRepository.findAll().stream().map(toModel()).toList();
    }

    /**
     * Creates a new {@link UserEntity} in the {@link #userRepository}.
     * <p>
     * This method is most likely to be executed when the frontend sends a request to create a new user.
     * This user is sent as a {@link UserModel} which then is transformed into a {@link UserEntity} object and saved in the
     * {@link #userRepository}.
     * <p>
     * Note that when a user with the given email already exists in the database it will throw an {@link UserEmailOccupiedException}.
     *
     * @param userModel the user that should be saved in the user repository.
     * @return the model saved in the database.
     * @throws UserEmailOccupiedException is thrown when any user already has this email registered.
     */
    @Transactional public @NotNull UserModel createUser(@NotNull UserModel userModel) throws UserEmailOccupiedException
    {
        userRepository.findUserByEmail(userModel.email()).map(toModel()).ifPresent(model ->
        {
            throw new UserEmailOccupiedException(model);
        });

        return toModel().apply(userRepository.save(toUser().apply(userModel)));
    }

    public @Unmodifiable @NotNull Set<? extends GrantedAuthority> getAuthorities(@NotNull UserModel userModel)
    {
        return toUser().apply(userModel).getAuthorities();
    }

    /**
     * Returns a {@link Function} which transforms a {@link UserEntity} into a {@link UserModel}.
     * <p>
     * This is useful when changing something from a user and send a reply to the front end as the {@link UserModel}
     * only functions as a translation for the frontend.
     *
     * @return the function to convert these two types.
     */
    @Contract(pure = true) private @org.jetbrains.annotations.NotNull Function<UserEntity, UserModel> toModel() {
        return user -> new UserModel(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), null /* safy safy yes yes */, user.isEnabled(), user.isLocked(), user.getGroups());
    }

    /**
     * Returns a {@link Function} which transforms a {@link UserModel} into a {@link UserEntity}.
     * <p>
     * This can be used to convert incoming frontend requests into an actual user.
     * As the Model only functions as connection between the front and backend.
     *
     * @return the function to convert these two.
     */
    @Contract(pure = true) private @NotNull Function<UserModel, UserEntity> toUser()
    {
        return userModel -> new UserEntity(userModel.id(), userModel.firstName(), userModel.lastName(), userModel.email(), passwordEncoder.encode(userModel.password()), userModel.enabled(), userModel.locked(), userModel.groupEntities());
    }
}
