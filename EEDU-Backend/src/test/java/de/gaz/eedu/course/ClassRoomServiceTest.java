package de.gaz.eedu.course;

import de.gaz.eedu.ArrayTestData;
import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.course.classroom.ClassRoomEntity;
import de.gaz.eedu.course.classroom.ClassRoomService;
import de.gaz.eedu.course.model.ClassRoomCreateModel;
import de.gaz.eedu.course.model.ClassRoomModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.entity.EntityService;
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

public class ClassRoomServiceTest extends ServiceTest<ClassRoomEntity, ClassRoomModel, ClassRoomCreateModel>
{
    /**
     * Is a necessary for all children of this class.
     * Most-likely this value is annotated using {@link Autowired} which
     * automatically provides
     * an instance of this {@link EntityService}.
     *
     * @param service which this tests should refer to.
     */
    public ClassRoomServiceTest(@Autowired @NotNull ClassRoomService service)
    {
        super(service);
    }

    @Contract(pure = true, value = "-> new") private static @NotNull Stream<ArrayTestData<Long>> getUser()
    {
        return Stream.of(new ArrayTestData<>(1, new Long[]{1L}),
                new ArrayTestData<>(2, new Long[]{1L}),
                new ArrayTestData<>(3, new Long[]{2L, 3L}));
    }

    @Override protected @NotNull Eval<ClassRoomCreateModel, ClassRoomModel> successEval()
    {
        ClassRoomCreateModel classRoomCreateModel = new ClassRoomCreateModel("5b");
        ClassRoomModel classRoomModel = new ClassRoomModel(5L, "5b", new UserModel[0], new CourseModel[0]);

        return Eval.eval(classRoomCreateModel, classRoomModel, (request, expect, result) ->
        {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertEquals(expect.name(), result.name());
            Assertions.assertArrayEquals(expect.users(), result.users());
            Assertions.assertArrayEquals(expect.courses(), result.courses());
        });
    }

    @Override protected @NotNull ClassRoomCreateModel occupiedCreateModel()
    {
        return new ClassRoomCreateModel("Q1");
    }

    @Transactional @ParameterizedTest(name = "{index} => data={0}") @MethodSource("getUser")
    public void testGetUsers(@NotNull ArrayTestData<Long> data)
    {
        test(Eval.eval(data.entityID(), data.expected(), Validator.arrayEquals()), request ->
        {
            Stream<UserEntity> userEntities = getService().loadEntityByIDSafe(data.entityID()).getUsers().stream();
            return userEntities.map(UserEntity::getId).toArray(Long[]::new);
        });
    }
}
