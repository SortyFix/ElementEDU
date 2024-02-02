package de.gaz.eedu.course.subjects;

import de.gaz.eedu.course.subjects.model.SubjectCreateModel;
import de.gaz.eedu.course.subjects.model.SubjectModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class SubjectService implements EntityService<SubjectRepository, SubjectEntity, SubjectModel, SubjectCreateModel>
{
    private final SubjectRepository subjectRepository;

    @Override
    public @NotNull SubjectRepository getRepository()
    {
        return subjectRepository;
    }

    @Override
    public @NotNull SubjectEntity createEntity(@NotNull SubjectCreateModel model) throws CreationException
    {
        return getRepository().save(model.toEntity(new SubjectEntity()));
    }
}
