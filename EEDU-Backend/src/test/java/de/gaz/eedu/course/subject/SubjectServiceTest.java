package de.gaz.eedu.course.subject;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.course.subjects.SubjectEntity;
import de.gaz.eedu.course.subjects.SubjectService;
import de.gaz.eedu.course.subjects.model.SubjectCreateModel;
import de.gaz.eedu.course.subjects.model.SubjectModel;
import de.gaz.eedu.entity.EntityService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

public class SubjectServiceTest extends ServiceTest<SubjectEntity, SubjectModel, SubjectCreateModel>
{
    /**
     * Is a necessary for all children of this class.
     * Most-likely this value is annotated using {@link Autowired} which
     * automatically provides
     * an instance of this {@link EntityService}.
     *
     * @param service which this tests should refer to.
     */
    public SubjectServiceTest(@Autowired @NotNull SubjectService service)
    {
        super(service);
    }

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
