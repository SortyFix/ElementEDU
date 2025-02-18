package de.gaz.eedu.course;

import de.gaz.eedu.ArrayTestData;
import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.course.appointment.entry.model.AppointmentEntryModel;
import de.gaz.eedu.course.appointment.frequent.model.FrequentAppointmentModel;
import de.gaz.eedu.course.model.CourseCreateModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.course.subject.model.SubjectModel;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.model.UserModel;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

@Getter(AccessLevel.PROTECTED)
public class CourseServiceTest extends ServiceTest<CourseService, CourseEntity, CourseModel, CourseCreateModel>
{
    @Autowired private CourseService service;

    @Contract(pure = true, value = "-> new") private static @NotNull Stream<ArrayTestData<Long>> getUserData()
    {
        return Stream.of(new ArrayTestData<>(1L, 1L), // comes from class
                new ArrayTestData<>(2L, 1L, 2L), // 1 comes from class, 2 is in course
                new ArrayTestData<>(3, 1L, 2L, 3L) // 1 is in course, 2 and 3 come from class
        );
    }

    @Override
    protected @NotNull Eval<CourseCreateModel, CourseModel> successEval()
    {
        CourseCreateModel create = new CourseCreateModel("7b-German", 1L, null, new Long[0]);

        CourseModel courseModel = new CourseModel(
                5L,
                "7b-German",
                new SubjectModel(1L, "German"),
                new AppointmentEntryModel[0],
                new FrequentAppointmentModel[0]
        );

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
        return new CourseCreateModel("Q1-German", 2L, null, new Long[0]);
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
