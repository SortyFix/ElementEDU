package de.gaz.eedu.course.subject;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.course.subject.model.SubjectCreateModel;
import de.gaz.eedu.course.subject.model.SubjectModel;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

@Getter(AccessLevel.PROTECTED)
public class SubjectServiceTest extends ServiceTest<Long, SubjectService, SubjectEntity, SubjectModel, SubjectCreateModel>
{
    @Autowired private SubjectService service;

    @Override
    protected @NotNull Eval<SubjectCreateModel, SubjectModel> successEval()
    {
        SubjectCreateModel subjectCreateModel = new SubjectCreateModel("Ethics");
        SubjectModel subjectModel = new SubjectModel(5L, "Ethics");

        return Eval.eval(subjectCreateModel, subjectModel, (request, expect, result) ->
        {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertEquals(expect.name(), result.name());
        });
    }

    @Override
    protected @NotNull SubjectCreateModel occupiedCreateModel()
    {
        return new SubjectCreateModel("Informatics");
    }
}
