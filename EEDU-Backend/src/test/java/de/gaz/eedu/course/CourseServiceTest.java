package de.gaz.eedu.course;

import de.gaz.eedu.ArrayTestData;
import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import de.gaz.eedu.course.model.CourseCreateModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.course.subjects.SubjectEntity;
import de.gaz.eedu.course.subjects.model.SubjectModel;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.model.UserModel;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

public class CourseServiceTest extends ServiceTest<CourseEntity, CourseModel, CourseCreateModel>
{
    public CourseServiceTest(@Autowired @NotNull CourseService courseService)
    {
        super(courseService);
    }

    @Contract(pure = true, value = "-> new") private static @NotNull Stream<ArrayTestData<Long>> getUserData()
    {
        return Stream.of(new ArrayTestData<>(1L, new Long[]{1L}), // comes from class
                new ArrayTestData<>(2L, new Long[]{
                        1L, 2L
                }), // 1 comes from class, 2 is in course
                new ArrayTestData<>(3, new Long[]{
                        1L, 2L, 3L
                }) // 1 is in course, 2 and 3 come from class
        );
    }

    @Contract(pure = true, value = "-> new") private static @NotNull Stream<TestData<String>> getSubjectData()
    {
        return Stream.of(new TestData<>(1L, "German"),
                new TestData<>(2L, "Mathematics"),
                new TestData<>(3L, "Informatics"),
                new TestData<>(3L, "German", false));
    }

    @Override
    protected @NotNull Eval<CourseCreateModel, CourseModel> successEval()
    {
        CourseCreateModel create = new CourseCreateModel("7b-German", 1L);

        SubjectModel subjectModel = new SubjectModel(1L, "German");
        CourseModel courseModel = new CourseModel(5L, "7b-German", subjectModel, new UserModel[0]);

        return Eval.eval(create, courseModel, (request, expect, result) ->
        {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertEquals(expect.name(), result.name());
            Assertions.assertEquals(expect.subject(), result.subject());
        });
    }

    @Override
    protected @NotNull CourseCreateModel occupiedCreateModel()
    {
        return new CourseCreateModel("Q1-German", 2L);
    }

    @Transactional @ParameterizedTest(name = "{index} => data={0}") @MethodSource("getSubjectData")
    public void testGetSubject(@NotNull TestData<String> data)
    {
        test(Eval.eval(data.entityID(), data.equalsResult(), Validator.equals()), request ->
        {
            SubjectEntity subject = getService().loadEntityByIDSafe(request).getSubject();
            return subject.getName().equals(data.expected());
        });
    }

    @Transactional @ParameterizedTest(name = "{index} => data={0}") @MethodSource("getUserData")
    public void testGetUsers(@NotNull ArrayTestData<Long> data)
    {
        test(Eval.eval(data.entityID(), data.expected(), Validator.arrayEquals()), request ->
        {
            Stream<UserEntity> userEntities = getService().loadEntityByIDSafe(data.entityID()).getUsers().stream();
            return userEntities.map(UserEntity::getId).toArray(Long[]::new);
        });
    }
}
