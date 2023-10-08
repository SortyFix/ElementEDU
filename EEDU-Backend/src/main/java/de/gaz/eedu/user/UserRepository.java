package de.gaz.eedu.user;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

/**
 * This class represents the repository that contains the users.
 * <p>
 * This class offers methods to receive or store users in the database.
 * This is connected to a {@link JpaRepository} from spring, which provides a convenient way to easily interact with the database.
 *
 * @author ivo
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @NotNull Optional<UserEntity> findUserByEmail(@NotNull String email);

    Optional<UserEntity> findByLoginName(@NotNull String loginName);

    @NotNull Collection<UserEntity> findUserByFirstName(@NotNull String firstname);

    @NotNull Collection<UserEntity> findUserByLastName(@NotNull String lastName);

}
