package de.gaz.eedu.course.subjects;

import de.gaz.eedu.course.subjects.model.SubjectCreateModel;
import de.gaz.eedu.course.subjects.model.SubjectModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.OccupiedException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class SubjectService extends EntityService<Long, SubjectRepository, SubjectEntity, SubjectModel, SubjectCreateModel>
{
    private final SubjectRepository subjectRepository;

    @Override
    public @NotNull SubjectRepository getRepository()
    {
        return subjectRepository;
    }

    @Override @Transactional
    public @NotNull List<SubjectEntity> createEntity(@NotNull Set<SubjectCreateModel> model) throws CreationException
    {
        if (getRepository().existsByNameIn(model.stream().map(SubjectCreateModel::name).toList()))
        {
            throw new OccupiedException();
        }

        Function<SubjectCreateModel, SubjectEntity> toEntity = current -> current.toEntity(new SubjectEntity());
        return saveEntity(model.stream().map(toEntity).toList());
    }
}
