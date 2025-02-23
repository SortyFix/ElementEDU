package de.gaz.eedu.course;

import de.gaz.eedu.ArrayTestData;
import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import de.gaz.eedu.course.appointment.entry.model.AppointmentEntryModel;
import de.gaz.eedu.course.appointment.frequent.model.FrequentAppointmentModel;
import de.gaz.eedu.course.classroom.ClassRoomService;
import de.gaz.eedu.course.classroom.model.ClassRoomModel;
import de.gaz.eedu.course.model.CourseCreateModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.course.subject.SubjectService;
import de.gaz.eedu.course.subject.model.SubjectModel;
import de.gaz.eedu.exception.AccountTypeMismatch;
import de.gaz.eedu.user.AccountType;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.model.ReducedUserModel;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Stream;

@Getter(AccessLevel.PROTECTED)
public class CourseServiceTest extends ServiceTest<Long, CourseService, CourseEntity, CourseModel, CourseCreateModel>
{
    @Autowired private CourseService service;
    @Autowired private ClassRoomService classRoomService;
    @Autowired private UserService userService;
    @Autowired private SubjectService subjectService;

    @Contract(pure = true, value = "-> new") private static @NotNull Stream<ArrayTestData<Long, Long>> getUserData()
    {
        return Stream.of(
                //1 is in class and course. Should not duplicate, others come from attached class
                new ArrayTestData<>(1L, 1L, 6L, 11L),

                // 1 is in course, others come from attached class
                new ArrayTestData<>(2L, 1L, 2L, 7L, 12L),
                new ArrayTestData<>(3L, 3L, 8L, 13L)
        );
    }

    @Override protected @NotNull TestData<Long, Boolean>[] deleteEntities()
    {
        //noinspection unchecked
        return new TestData[] {new TestData<>(10L, true), new TestData<>(11L, false)};
    }

    @Override
    protected @NotNull Eval<CourseCreateModel, CourseModel> successEval()
    {
        String name = "course9";
        ReducedUserModel teacher = new ReducedUserModel(6L, "User", "5", AccountType.TEACHER);
        ClassRoomModel classRoom = getClassRoomService().loadByIdSafe(1L);
        SubjectModel subject = new SubjectModel("subject9");

        FrequentAppointmentModel[] frequent = new FrequentAppointmentModel[0];
        AppointmentEntryModel[] appointments = new AppointmentEntryModel[0];

        CourseCreateModel create = new CourseCreateModel(name, subject.id(), teacher.id(), new Long[0], classRoom.id());
        CourseModel courseModel = new CourseModel(11L, name, subject, new ReducedUserModel[0], appointments, frequent, teacher, classRoom);
        return Eval.eval(create, courseModel, (request, expect, result) ->
        {
            Assertions.assertEquals(expect, result);
            Assertions.assertEquals(expect.name(), result.name());
            Assertions.assertEquals(expect.subject(), result.subject());
            Assertions.assertEquals(expect.classRoom(), result.classRoom());
            Assertions.assertEquals(expect.teacher(), result.teacher());

            Assertions.assertArrayEquals(expect.students(), result.students());
            Assertions.assertArrayEquals(expect.appointmentEntries(), result.appointmentEntries());
            Assertions.assertArrayEquals(expect.frequentAppointments(), result.frequentAppointments());
        });
    }

    @Override
    protected @NotNull CourseCreateModel occupiedCreateModel()
    {
        return new CourseCreateModel("course0", "subject0", 4L, new Long[0], null);
    }

    @Transactional @ParameterizedTest(name = "{index} => data={0}") @MethodSource("getUserData")
    public void testGetUsers(@NotNull ArrayTestData<Long, Long> data)
    {
        test(Eval.eval(data.entityID(), data.expected(), Validator.exactArrayEquals()), request ->
        {
            Set<ReducedUserModel> reducedUserModels = getService().loadReducedModelsByCourse(request);
            return reducedUserModels.stream().map(ReducedUserModel::id).toArray(Long[]::new);
        });
    }

    @ParameterizedTest(name = "{index} => data={0}") @ValueSource(longs = {5, 6})
    public void testSetTeacher(long userId)
    {
        UserEntity userEntity = getUserService().loadEntityByIDSafe(userId);
        CourseEntity courseEntity = getService().loadEntityByIDSafe(6L);

        if(userId == 5)
        {
            Assertions.assertThrowsExactly(AccountTypeMismatch.class, () -> courseEntity.setTeacher(userEntity));
            return;
        }

        test(Eval.eval(userEntity, true, Validator.equals()), courseEntity::setTeacher);
        test(Eval.eval(userEntity, false, Validator.equals()), courseEntity::setTeacher);
    }
}
