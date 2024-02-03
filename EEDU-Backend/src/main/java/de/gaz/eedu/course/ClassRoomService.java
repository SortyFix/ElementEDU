package de.gaz.eedu.course;

import de.gaz.eedu.course.model.ClassRoomCreateModel;
import de.gaz.eedu.course.model.ClassRoomModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.user.UserEntity;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
            Long[] userIds = classRoom.getUsers().stream().map(UserEntity::getId).toArray(Long[]::new);
            classRoom.detachUser(this, userIds);

            Set<CourseEntity> courses = classRoom.getCourses();
            courses.forEach(course -> course.disassociateClassroom(getCourseService()));

            getRepository().deleteById(id);

            //TODO log
            return true;
        }).orElse(false);
    }
}
