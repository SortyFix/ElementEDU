package de.gaz.eedu.course.classroom;

import de.gaz.eedu.ArrayTestData;
import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.CourseRepository;
import de.gaz.eedu.course.classroom.model.ClassRoomCreateModel;
import de.gaz.eedu.course.classroom.model.ClassRoomModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.user.AccountType;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.model.ReducedUserModel;
import de.gaz.eedu.user.repository.UserRepository;
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

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@Getter(AccessLevel.PROTECTED)
public class ClassRoomServiceTest extends ServiceTest<String, ClassRoomService, ClassRoomEntity, ClassRoomModel, ClassRoomCreateModel>
{

    @Autowired private ClassRoomService service;
    @Autowired private CourseRepository courseRepository;
    @Autowired private UserRepository userRepository;

    @Contract(pure = true, value = "-> new") private static @NotNull Stream<ArrayTestData<String, Long>> getUser()
    {
        return Stream.of(
                new ArrayTestData<>("classroom0", 1L, 6L, 11L),
                new ArrayTestData<>("classroom1", 2L, 7L, 12L),
                new ArrayTestData<>("classroom5", new Long[]{})
        );
    }

    @Override protected @NotNull TestData<String, Boolean>[] deleteEntities()
    {
        //noinspection unchecked
        return new TestData[]{new TestData<>("classroom9", true), new TestData<>("classroom10", false)};
    }

    @Override protected @NotNull Validator<String, Boolean> deletePipeline(@NotNull TestData<String, Boolean> data)
    {
        boolean isEntity = Objects.equals(data.entityID(), "classroom9");
        long course = 9, user = 18;

        Assertions.assertEquals(getCourseRepository().findById(course).orElseThrow().hasClassRoomAssigned(), isEntity);
        Assertions.assertEquals(getUserRepository().findById(user).orElseThrow().hasClassRoomAssigned(), isEntity);

        return ((request, expect, result) ->
        {
            Assertions.assertFalse(getCourseRepository().findById(course).orElseThrow().hasClassRoomAssigned());
            Assertions.assertFalse(getUserRepository().findById(user).orElseThrow().hasClassRoomAssigned());
        });
    }

    @Override protected @NotNull Eval<ClassRoomCreateModel, ClassRoomModel> successEval()
    {
        ReducedUserModel teacher = new ReducedUserModel(6L, "User", "5", AccountType.TEACHER);
        CourseModel courseModel = getCourseRepository().findById(1L).map(CourseEntity::toModel).orElseThrow();
        return Eval.eval(
                new ClassRoomCreateModel("classroom10", teacher.id(), new Long[0], new Long[]{courseModel.id()}),
                new ClassRoomModel("classroom10", new ReducedUserModel[0], teacher),
                (request, expect, result) ->
                {
                    Assertions.assertEquals(expect, result);
                    Assertions.assertEquals(expect.tutor(), result.tutor());
                    Assertions.assertArrayEquals(expect.students(), result.students());
                });
    }

    @Override protected @NotNull ClassRoomCreateModel occupiedCreateModel()
    {
        return new ClassRoomCreateModel("classroom0", 5L, new Long[0], new Long[0]);
    }

    @Transactional @ParameterizedTest(name = "{index} => data={0}") @MethodSource("getUser")
    public void testGetUsers(@NotNull ArrayTestData<String, Long> data)
    {
        test(
                Eval.eval(data.entityID(), data.expected(), Validator.exactArrayEquals()), request ->
                {

                    Set<ReducedUserModel> reducedUserModels = getService().loadReducedModelsByClass(request);
                    return reducedUserModels.stream().map(ReducedUserModel::id).toArray(Long[]::new);
                });
    }

    @Transactional @ParameterizedTest(name = "{index} => data={0}") @MethodSource("getUser")
    public void testGetUsersByEntity(@NotNull ArrayTestData<String, Long> data)
    {
        test(
                Eval.eval(data.entityID(), data.expected(), Validator.arrayEquals()), request ->
                {
                    ClassRoomEntity classRoom = getService().loadEntityByIDSafe(request);
                    Stream<UserEntity> students = classRoom.getStudents().stream();

                    return Stream.concat(
                            students,
                            classRoom.getTutor().stream()).map(UserEntity::getId).toArray(Long[]::new);
                });
    }
}
