package de.gaz.eedu.course;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.course.model.ClassRoomCreateModel;
import de.gaz.eedu.course.model.ClassRoomModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.user.model.UserModel;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Override
    protected @NotNull Eval<ClassRoomCreateModel, ClassRoomModel> successEval()
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

    @Override
    protected @NotNull ClassRoomCreateModel occupiedCreateModel()
    {
        return new ClassRoomCreateModel("Q1");
    }
}
