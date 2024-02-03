package de.gaz.eedu.course;

import de.gaz.eedu.course.model.CourseCreateModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.course.subjects.SubjectService;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.NameOccupiedException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CourseService implements EntityService<CourseRepository, CourseEntity, CourseModel, CourseCreateModel>
{
    private final CourseRepository courseRepository;
    @Getter(AccessLevel.PROTECTED)
    private final SubjectService subjectService;

    @Override
    public @NotNull CourseRepository getRepository()
    {
        return courseRepository;
    }

    @Transactional
    @Override
    public @NotNull CourseEntity createEntity(@NotNull CourseCreateModel model) throws CreationException
    {
        if(getRepository().existsByName(model.name()))
        {
            throw new NameOccupiedException(model.name());
        }

        return saveEntity(model.toEntity(new CourseEntity(), (entity) ->
        {
            // Remove this course from the subject
            entity.setSubject(getSubjectService().loadEntityByIDSafe(model.subjectId()));

            // Remove this course from its class
            entity.disassociateClassroom();

            return entity;
        }));
    }
}
