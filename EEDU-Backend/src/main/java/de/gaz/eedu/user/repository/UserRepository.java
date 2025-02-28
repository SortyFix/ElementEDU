package de.gaz.eedu.user.repository;

import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.model.ReducedUserModel;
import jakarta.validation.constraints.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * This class represents the repository that contains the users.
 * <p>
 * This class offers methods to receive or store users in the database.
 * This is connected to a {@link JpaRepository} from spring, which provides a convenient way to easily interact with the database.
 *
 * @author ivo
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, UserEntityRepository
{
    @NotNull Optional<UserEntity> findByLoginName(@NotNull String loginName);

    @NotNull boolean existsByLoginNameIn(@NotNull Collection<String> loginNames);

    @Query(
            "SELECT new de.gaz.eedu.user.model.ReducedUserModel(u.id, u.firstName, u.lastName, u.accountType) " +
                    "FROM UserEntity u " +
                    "WHERE u.id = :userId"
    ) @NotNull Optional<ReducedUserModel> findReducedById(long userId);

    @Query(
            "SELECT new de.gaz.eedu.user.model.ReducedUserModel(u.id, u.firstName, u.lastName, u.accountType) " +
                    "FROM UserEntity u"
    ) @Unmodifiable @NotNull Set<ReducedUserModel> findAllReduced();

}
