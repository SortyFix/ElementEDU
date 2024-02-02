package de.gaz.eedu.course;

import de.gaz.eedu.course.model.CourseCreateModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.course.subjects.SubjectService;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
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
        return saveEntity(model.toEntity(new CourseEntity(), (entity) ->
        {
            entity.setSubjectEntity(getSubjectService().loadEntityByIDSafe(model.subjectId()));
            return entity;
        }));
    }
}
