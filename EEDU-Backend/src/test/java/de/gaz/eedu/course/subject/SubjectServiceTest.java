package de.gaz.eedu.course.subject;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import de.gaz.eedu.course.subject.model.SubjectCreateModel;
import de.gaz.eedu.course.subject.model.SubjectModel;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

@Getter(AccessLevel.PROTECTED)
public class SubjectServiceTest extends ServiceTest<String, SubjectService, SubjectEntity, SubjectModel, SubjectCreateModel>
{
    @Autowired private SubjectService service;

    @Override protected @NotNull TestData<String, Boolean>[] deleteEntities()
    {
        //noinspection unchecked
        return new TestData[] {
                new TestData<>("subject9", true),
                new TestData<>("subject10", false)
        };
    }

    @Override
    protected @NotNull Eval<SubjectCreateModel, SubjectModel> successEval()
    {
        SubjectCreateModel subjectCreateModel = new SubjectCreateModel("subject9");
        SubjectModel subjectModel = new SubjectModel("subject9");

        return Eval.eval(subjectCreateModel, subjectModel, (request, expect, result) ->
                Assertions.assertEquals(expect, result)
        );
    }

    @Override
    protected @NotNull SubjectCreateModel occupiedCreateModel()
    {
        return new SubjectCreateModel("subject0");
    }
}
