package de.gaz.eedu.course.subject;

import de.gaz.eedu.course.subject.model.SubjectCreateModel;
import de.gaz.eedu.course.subject.model.SubjectModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.OccupiedException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubjectService extends EntityService<String, SubjectRepository, SubjectEntity, SubjectModel, SubjectCreateModel>
{
    @Getter(AccessLevel.PROTECTED) private final SubjectRepository repository;

    @Override @Transactional
    public @NotNull List<SubjectEntity> createEntity(@NotNull Set<SubjectCreateModel> model) throws CreationException
    {
        if (getRepository().existsByIdIn(model.stream().map(SubjectCreateModel::id).toList()))
        {
            throw new OccupiedException();
        }

        List<SubjectEntity> subjectEntities = model.stream().map(current ->
        {
            SubjectEntity subject = new SubjectEntity(current.id());
            return current.toEntity(subject);
        }).toList();
        return saveEntity(subjectEntities);
    }
}
