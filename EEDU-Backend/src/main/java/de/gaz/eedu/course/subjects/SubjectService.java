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

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class SubjectService extends EntityService<SubjectRepository, SubjectEntity, SubjectModel, SubjectCreateModel>
{
    private final SubjectRepository subjectRepository;

    @Override
    public @NotNull SubjectRepository getRepository()
    {
        return subjectRepository;
    }

    @Override @Transactional
    public @NotNull SubjectEntity[] createEntity(@NotNull SubjectCreateModel... model) throws CreationException
    {
        List<String> subjects = Arrays.stream(model).map(SubjectCreateModel::name).toList();
        boolean duplicates = subjects.size() != subjects.stream().collect(Collectors.toUnmodifiableSet()).size();
        if (getRepository().existsByNameIn(subjects) || duplicates)
        {
            throw new OccupiedException();
        }

        SubjectEntity[] entities = new SubjectEntity[subjects.size()];
        Function<SubjectCreateModel, SubjectEntity> toEntity = current -> current.toEntity(new SubjectEntity());
        return saveEntity(Stream.of(model).map(toEntity).collect(Collectors.toList())).toArray(entities);
    }
}
