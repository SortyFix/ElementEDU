package de.gaz.eedu.course;

import de.gaz.eedu.user.model.ReducedUserModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long>
{

    @Query(
            "SELECT c FROM CourseEntity c " +
                    "LEFT JOIN FETCH c.frequentAppointments sA " +
                    "LEFT JOIN FETCH c.appointments a " +
                    "LEFT JOIN FETCH c.classRoom r" +
                    "LEFT JOIN FETCH c.subject s " +
                    "LEFT JOIN FETCH c.users u " +
                    "LEFT JOIN FETCH c.repository rE " +
                    "WHERE c.id = :id"
    ) @Override @NotNull Optional<CourseEntity> findById(@NotNull Long id);


    @Query(
            "SELECT new de.gaz.eedu.user.model.ReducedUserModel(u.id, u.firstName, u.lastName, u.accountType) " +
                    "FROM CourseEntity c JOIN c.users u WHERE c.id = :course"
    ) @NotNull @Unmodifiable Set<ReducedUserModel> findAllReducedUsersByCourse(long course);

    @Query(
            "SELECT c FROM CourseEntity c " +
                    "LEFT JOIN FETCH c.classRoom " +
                    "LEFT JOIN FETCH c.frequentAppointments " +
                    "LEFT JOIN FETCH c.appointments " +
                    "LEFT JOIN FETCH c.subject " +
                    "LEFT JOIN FETCH c.users u " +
                    "LEFT JOIN FETCH c.repository rE " +
                    "LEFT JOIN FETCH rE.privilege " +
                    "LEFT JOIN FETCH rE.tags " +
                    "WHERE :userId IN (SELECT u.id FROM c.users u)"
    ) @NotNull @Unmodifiable Set<CourseEntity> findAllByUserId(long userId);

    boolean existsByName(@NotNull String name);

    boolean existsByNameIn(@NotNull Collection<String> names);
}
