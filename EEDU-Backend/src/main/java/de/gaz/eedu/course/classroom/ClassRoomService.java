package de.gaz.eedu.course.classroom;

import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.CourseService;
import de.gaz.eedu.course.classroom.model.ClassRoomCreateModel;
import de.gaz.eedu.course.classroom.model.ClassRoomModel;
import de.gaz.eedu.course.subjects.model.SubjectCreateModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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

    @Transactional @Override public @NotNull List<ClassRoomEntity> createEntity(@NotNull Set<ClassRoomCreateModel> model) throws CreationException
    {
        if (getRepository().existsByNameIn(model.stream().map(ClassRoomCreateModel::name).toList()))
        {
            throw new OccupiedException();
        }

        Set<CourseEntity> courses = new HashSet<>();
        List<ClassRoomEntity> classRoomEntities = saveEntity(model.stream().map(current ->
        {
            ClassRoomEntity classRoom = current.toEntity(new ClassRoomEntity(), (entity ->
            {
                // attach users
                if (current.users().length > 0)
                {
                    List<UserEntity> userEntities = getUserRepository().findAllById(List.of(current.users()));
                    entity.attachStudents(userEntities.toArray(UserEntity[]::new));
                }

                return entity;
            }));

            Set<CourseEntity> courseEntities = getCourseService().loadEntityById(current.courses());
            courses.addAll(courseEntities);
            courseEntities.forEach(courseEntity -> courseEntity.linkClassRoom(classRoom));
            return classRoom;
        }).toList());

        // save linked courses after newly created classes have been saved
        getCourseService().saveEntity(courses);
        return classRoomEntities;
    }

    @Override public void deleteRelations(@NotNull ClassRoomEntity entry)
    {
        // remove this classroom from all courses
        Set<CourseEntity> courses = entry.getCourses();
        courses.forEach(CourseEntity::unlinkClassRoom);
        getCourseService().saveEntity(courses);
    }
}
