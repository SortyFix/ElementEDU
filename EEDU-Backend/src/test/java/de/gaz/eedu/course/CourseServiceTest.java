package de.gaz.eedu.course;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.course.model.CourseCreateModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.course.subjects.model.SubjectModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.user.model.UserModel;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

public class CourseServiceTest extends ServiceTest<CourseEntity, CourseModel, CourseCreateModel>
{
    /**
     * Is a necessary for all children of this class.
     * Most-likely this value is annotated using {@link Autowired} which
     * automatically provides
     * an instance of this {@link EntityService}.
     *
     * @param service which this tests should refer to.
     */
    public CourseServiceTest(@Autowired @NotNull CourseService courseService)
    {
        super(courseService);
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
}
