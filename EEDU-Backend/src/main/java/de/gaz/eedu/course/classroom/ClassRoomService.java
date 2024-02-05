package de.gaz.eedu.course.classroom;

import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.CourseService;
import de.gaz.eedu.course.classroom.model.ClassRoomCreateModel;
import de.gaz.eedu.course.classroom.model.ClassRoomModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.user.UserEntity;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

@RequiredArgsConstructor
@Service
@Getter
public class ClassRoomService implements EntityService<ClassRoomRepository, ClassRoomEntity, ClassRoomModel, ClassRoomCreateModel>
{
    @Getter(AccessLevel.NONE)
    private final ClassRoomRepository classRoomRepository;
    private final CourseService courseService;

    @Override
    public @NotNull ClassRoomRepository getRepository()
    {
        return classRoomRepository;
    }

    @Transactional
    @Override
    public @NotNull ClassRoomEntity createEntity(@NotNull ClassRoomCreateModel model) throws CreationException
    {
        if (getRepository().existsByName(model.name()))
        {
            throw new NameOccupiedException(model.name());
        }

        return saveEntity(model.toEntity(new ClassRoomEntity()));
    }

    @Transactional
    @Override
    public boolean delete(long id)
    {
        return loadEntityByID(id).map(classRoom ->
        {
            // Remove users from this class
            Long[] userIds = classRoom.getStudents().stream().map(UserEntity::getId).toArray(Long[]::new);
            classRoom.detachStudents(this, userIds);

            Set<CourseEntity> courses = classRoom.getCourses();
            courses.forEach(course -> course.revokeClassroom(getCourseService()));

            getRepository().deleteById(id);

            String deleteMessage = "Deleted class room {} from the system. Additionally, the class room has been disassociated from {} users and {} courses.";
            LoggerFactory.getLogger(ClassRoomService.class).info(deleteMessage,
                    classRoom.getId(),
                    userIds.length,
                    courses.size());
            return true;
        }).orElse(false);
    }
}
