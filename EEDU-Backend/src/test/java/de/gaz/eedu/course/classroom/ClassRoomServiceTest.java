package de.gaz.eedu.course.classroom;

import de.gaz.eedu.ArrayTestData;
import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.course.classroom.model.ClassRoomCreateModel;
import de.gaz.eedu.course.classroom.model.ClassRoomModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.model.ReducedUserModel;
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
public class ClassRoomServiceTest extends ServiceTest<ClassRoomService, ClassRoomEntity, ClassRoomModel, ClassRoomCreateModel> {

    @Autowired private ClassRoomService service;

    @Contract(pure = true, value = "-> new")
    private static @NotNull Stream<ArrayTestData<Long>> getUser() {
        return Stream.of(new ArrayTestData<>(1, 1L), new ArrayTestData<>(2, 2L, 3L), new ArrayTestData<>(3, new Long[]{}));
    }

    @Override
    protected @NotNull Eval<ClassRoomCreateModel, ClassRoomModel> successEval() {
        ClassRoomCreateModel classRoomCreateModel = new ClassRoomCreateModel("5b", new Long[0], new Long[0], 2L);
        ClassRoomModel classRoomModel = new ClassRoomModel(5L, "5b", new ReducedUserModel[0], new CourseModel[0], null);

        return Eval.eval(classRoomCreateModel, classRoomModel, (request, expect, result) -> {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertEquals(expect.name(), result.name());
            Assertions.assertArrayEquals(expect.students(), result.students());
            Assertions.assertArrayEquals(expect.courses(), result.courses());
        });
    }

    @Override
    protected @NotNull ClassRoomCreateModel occupiedCreateModel() {
        return new ClassRoomCreateModel("Q1", new Long[0], new Long[0], 2L);
    }

    @Transactional
    @ParameterizedTest(name = "{index} => data={0}")
    @MethodSource("getUser")
    public void testGetUsers(@NotNull ArrayTestData<Long> data) {
        test(Eval.eval(data.entityID(), data.expected(), Validator.arrayEquals()), request -> {
            Stream<UserEntity> userEntities = getService().loadEntityByIDSafe(data.entityID()).getStudents().stream();
            return userEntities.map(UserEntity::getId).toArray(Long[]::new);
        });
    }
}
