package de.gaz.eedu.course;

import de.gaz.eedu.course.classroom.ClassRoomRepository;
import de.gaz.eedu.course.model.CourseCreateModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.course.subjects.SubjectService;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
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

@RequiredArgsConstructor @Service @Getter(AccessLevel.PROTECTED)
public class CourseService extends EntityService<CourseRepository, CourseEntity, CourseModel, CourseCreateModel>
{
    @Getter(AccessLevel.NONE) private final CourseRepository courseRepository;
    private final SubjectService subjectService;

    private final UserRepository userRepository;
    private final ClassRoomRepository classRoomRepository;

    @Override public @NotNull CourseRepository getRepository()
    {
        return courseRepository;
    }

    @Transactional @Override public @NotNull CourseEntity createEntity(
            @NotNull CourseCreateModel model) throws CreationException
    {
        if (getRepository().existsByName(model.name()))
        {
            throw new NameOccupiedException(model.name());
        }

        return saveEntity(model.toEntity(new CourseEntity(), (entity) ->
        {
            // attach users
            if(model.users().length > 0)
            {
                List<UserEntity> userEntities = getUserRepository().findAllById(List.of(model.users()));
                entity.attachUsers(userEntities.toArray(UserEntity[]::new));
            }

            // assign class
            if (model.classId() != null)
            {
                getClassRoomRepository().findById(model.classId()).ifPresentOrElse(entity::assignClassRoom, () ->
                {
                    throw new EntityUnknownException(model.classId());
                });
            }

            // add to subject
            entity.setSubject(getSubjectService().loadEntityByIDSafe(model.subjectId()));
            return entity;
        }));
    }
}
