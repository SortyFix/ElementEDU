package de.gaz.eedu.course.subject;

import de.gaz.eedu.ArrayTestData;
import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import de.gaz.eedu.course.subjects.SubjectEntity;
import de.gaz.eedu.course.subjects.SubjectService;
import de.gaz.eedu.course.subjects.model.SubjectCreateModel;
import de.gaz.eedu.course.subjects.model.SubjectModel;
import de.gaz.eedu.entity.EntityService;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

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

    @Contract(pure = true, value = "-> new") private static @NotNull Stream<TestData<String>> getSubjectName()
    {
        return Stream.of(new TestData<>(1L, "German"),
                new TestData<>(2L, "Mathematics"),
                new TestData<>(3L, "Informatics"),
                new TestData<>(3L, "German", false));
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

    @ParameterizedTest(name = "{index} => request={0}") @MethodSource("getSubjectName")
    public void testGetSubjectName(@NotNull TestData<String> data)
    {
        test(Eval.eval(data.entityID(), data.equalsResult(), Validator.equals()), request ->
        {
            SubjectEntity subjectEntity = getService().loadEntityByIDSafe(request);
            return subjectEntity.getName().equals(data.expected());
        });
    }

    @Override
    protected @NotNull SubjectCreateModel occupiedCreateModel()
    {
        return new SubjectCreateModel("Informatics");
    }
}
