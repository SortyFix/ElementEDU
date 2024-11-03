package de.gaz.eedu.course.classroom;

import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.CourseService;
import de.gaz.eedu.course.classroom.model.ClassRoomCreateModel;
import de.gaz.eedu.course.classroom.model.ClassRoomModel;
import de.gaz.eedu.course.subjects.model.SubjectCreateModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Transactional @Override public @NotNull ClassRoomEntity[] createEntity(@NotNull ClassRoomCreateModel... model) throws CreationException
    {
        List<String> clazzes = Arrays.stream(model).map(ClassRoomCreateModel::name).toList();
        boolean duplicates = clazzes.size() != clazzes.stream().collect(Collectors.toUnmodifiableSet()).size();
        if(getRepository().existsByNameIn(clazzes) || duplicates)
        {
            throw new OccupiedException();
        }

        return Stream.of(model).map(current ->
        {
            ClassRoomEntity classRoom = saveEntity(current.toEntity(new ClassRoomEntity(), (entity ->
            {
                // attach users
                if (current.users().length > 0)
                {
                    List<UserEntity> userEntities = getUserRepository().findAllById(List.of(current.users()));
                    entity.attachStudents(userEntities.toArray(UserEntity[]::new));
                }

                return entity;
            })));

            // add classroom to courses after entity was saved.
            Set<CourseEntity> courseEntities = getCourseService().loadEntityById(current.courses());
            courseEntities.forEach(courseEntity -> courseEntity.linkClassRoom(classRoom));
            getCourseService().saveEntity(courseEntities);
            return classRoom;
        }).toArray(ClassRoomEntity[]::new);
    }

    @Override public void deleteRelations(@NotNull ClassRoomEntity entry)
    {
        // remove this classroom from all courses
        Set<CourseEntity> courses = entry.getCourses();
        courses.forEach(CourseEntity::unlinkClassRoom);
        getCourseService().saveEntity(courses);
    }
}
