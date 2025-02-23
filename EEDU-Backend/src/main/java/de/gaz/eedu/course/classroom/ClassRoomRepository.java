package de.gaz.eedu.course.classroom;

import de.gaz.eedu.course.model.CourseModel;
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
public interface ClassRoomRepository extends JpaRepository<ClassRoomEntity, Long>
{

    @Query("SELECT c FROM ClassRoomEntity c LEFT JOIN FETCH c.users LEFT JOIN FETCH c.courses WHERE c.id = :id")
    @Override @NotNull Optional<ClassRoomEntity> findById(@NotNull Long id);

    @Query("SELECT COUNT(u) > 0 FROM CourseEntity c JOIN c.users u WHERE c.id = :courseId AND u.id = :userId")
    boolean existsUserInCourse(long userId, long courseId);

    @Query("SELECT co FROM ClassRoomEntity c JOIN c.courses co WHERE c.id = :id")
    @NotNull @Unmodifiable Set<CourseModel> findAllCoursesById(long id);

    @Query(
            "SELECT new de.gaz.eedu.user.model.ReducedUserModel(u.id, u.firstName, u.lastName, u.accountType) " +
            "FROM ClassRoomEntity c " +
            "JOIN c.users u " +
            "WHERE c.id = :classroom"
    ) @NotNull @Unmodifiable Set<ReducedUserModel> findAllUsersByClass(long classroom);

    boolean existsByNameIn(@NotNull Collection<String> name);
}
