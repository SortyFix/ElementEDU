package de.gaz.eedu.course.classroom;

import de.gaz.eedu.ArrayTestData;
import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import de.gaz.eedu.course.classroom.model.ClassRoomCreateModel;
import de.gaz.eedu.course.classroom.model.ClassRoomModel;
import de.gaz.eedu.user.AccountType;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.model.ReducedUserModel;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@Getter(AccessLevel.PROTECTED)
public class ClassRoomServiceTest extends ServiceTest<Long, ClassRoomService, ClassRoomEntity, ClassRoomModel, ClassRoomCreateModel>
{

    @Autowired private ClassRoomService service;

    @Override protected @NotNull TestData<Long, Boolean>[] deleteEntities()
    {
        //noinspection unchecked
        return new TestData[] {
            new TestData<>(10, true),
            new TestData<>(11, false)
        };
    }

    @Contract(pure = true, value = "-> new") private static @NotNull Stream<ArrayTestData<Long, Long>> getUser()
    {
        return Stream.of(
                new ArrayTestData<>(1L, 1L, 6L, 11L),
                new ArrayTestData<>(2L, 2L, 7L, 12L),
                new ArrayTestData<>(6L, new Long[]{})
        );
    }

    @Override protected @NotNull Eval<ClassRoomCreateModel, ClassRoomModel> successEval()
    {
        ReducedUserModel teacher = new ReducedUserModel(6L, "User", "5", AccountType.TEACHER);

        return Eval.eval(
                new ClassRoomCreateModel("classroom10", teacher.id(), new Long[0], new Long[0]),
                new ClassRoomModel(11L, "classroom10", new ReducedUserModel[0], teacher),
                (request, expect, result) ->
                {
                    Assertions.assertEquals(expect, result);
                    Assertions.assertEquals(expect.name(), result.name());
                    Assertions.assertEquals(expect.tutor(), result.tutor());
                    Assertions.assertArrayEquals(expect.students(), result.students());
                });
    }

    @Override protected @NotNull ClassRoomCreateModel occupiedCreateModel()
    {
        return new ClassRoomCreateModel("classroom0", 5L, new Long[0], new Long[0]);
    }

    @Transactional @ParameterizedTest(name = "{index} => data={0}") @MethodSource("getUser")
    public void testGetUsers(@NotNull ArrayTestData<Long, Long> data)
    {
        test(Eval.eval(data.entityID(), data.expected(), Validator.exactArrayEquals()), request -> {

            Set<ReducedUserModel> reducedUserModels = getService().loadReducedModelsByClass(request);
            return reducedUserModels.stream().map(ReducedUserModel::id).toArray(Long[]::new);
        });
    }

    @Transactional @ParameterizedTest(name = "{index} => data={0}") @MethodSource("getUser")
    public void testGetUsersByEntity(@NotNull ArrayTestData<Long, Long> data)
    {
        test(Eval.eval(data.entityID(), data.expected(), Validator.arrayEquals()), request -> {
            ClassRoomEntity classRoom = getService().loadEntityByIDSafe(request);
            Stream<UserEntity> students = classRoom.getStudents().stream();

            return Stream.concat(students, classRoom.getTutor().stream()).map(UserEntity::getId).toArray(Long[]::new);
        });
    }
}
