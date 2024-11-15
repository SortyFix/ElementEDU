package de.gaz.eedu.course.classroom;

import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.CourseService;
import de.gaz.eedu.course.classroom.model.ClassRoomCreateModel;
import de.gaz.eedu.course.classroom.model.ClassRoomModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor @Service @Getter
public class ClassRoomService extends EntityService<ClassRoomRepository, ClassRoomEntity, ClassRoomModel, ClassRoomCreateModel>
{
    @Getter(AccessLevel.NONE) private final ClassRoomRepository classRoomRepository;
    private final CourseService courseService;
    private final UserRepository userRepository;

    @Override public @NotNull ClassRoomRepository getRepository()
    {
        return classRoomRepository;
    }

    @Transactional @Override public @NotNull ClassRoomEntity createEntity(@NotNull ClassRoomCreateModel model) throws CreationException
    {
        if (getRepository().existsByName(model.name()))
        {
            throw new NameOccupiedException(model.name());
        }

        ClassRoomEntity classRoomEntity = saveEntity(model.toEntity(new ClassRoomEntity(), (entity) ->
        {
            // attach users
            if (model.users().length > 0)
            {
                List<UserEntity> userEntities = getUserRepository().findAllById(List.of(model.users()));
                entity.attachStudents(userEntities.toArray(UserEntity[]::new));
            }

            return entity;
        }));

        // add classroom to courses after entity was saved.
        // assign courses
        Set<CourseEntity> courseEntities = getCourseService().loadEntityById(model.courses());
        courseEntities.forEach(courseEntity -> courseEntity.linkClassRoom(classRoomEntity));
        getCourseService().saveEntity(courseEntities);

        return classRoomEntity;
    }

    @Override public void deleteRelations(@NotNull ClassRoomEntity entry)
    {
        // remove this classroom from all courses
        Set<CourseEntity> courses = entry.getCourses();
        courses.forEach(CourseEntity::unlinkClassRoom);
        getCourseService().saveEntity(courses);
    }
}
