package de.gaz.eedu.user;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @NotNull Optional<UserEntity> findByLoginName(@NotNull String loginName);

    @NotNull boolean existsByLoginNameIn(@NotNull Collection<String> loginNames);

    @Query("SELECT u FROM UserEntity u " +
            "LEFT JOIN FETCH u.groups g " +
            "LEFT JOIN FETCH g.privileges p " +
            "LEFT JOIN FETCH u.themeEntity t " +
            "WHERE u.id = :userId")
    @NotNull Optional<UserEntity> findByIdEagerly(@Param("userId") long userId);


    @Query("SELECT u FROM UserEntity u " +
            "LEFT JOIN FETCH u.groups g " +
            "LEFT JOIN FETCH g.privileges p " +
            "LEFT JOIN FETCH u.themeEntity t")
    @NotNull Set<UserEntity> findAllEagerly();

}
