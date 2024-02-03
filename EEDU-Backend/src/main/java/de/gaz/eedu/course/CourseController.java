package de.gaz.eedu.course;

import de.gaz.eedu.course.model.CourseCreateModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.entity.EntityController;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/course")
@RequiredArgsConstructor
public class CourseController extends EntityController<CourseService, CourseModel, CourseCreateModel>
{
    private final CourseService courseService;

    @Override
    protected @NotNull CourseService getEntityService()
    {
        return courseService;
    }

    @PostMapping("/create")
    @Override
    public @NotNull ResponseEntity<CourseModel> create(@NotNull @RequestBody CourseCreateModel model)
    {
        return super.create(model);
    }

    @DeleteMapping("/delete/{id}")
    @Override
    public @NotNull Boolean delete(@NotNull @PathVariable Long id)
    {
        return super.delete(id);
    }

    @GetMapping("/get/{id}")
    @Override
    public @NotNull ResponseEntity<CourseModel> getData(@NotNull @PathVariable Long id)
    {
        return super.getData(id);
    }
}
