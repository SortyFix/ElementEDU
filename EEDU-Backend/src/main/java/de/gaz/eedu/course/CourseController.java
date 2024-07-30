package de.gaz.eedu.course;

import de.gaz.eedu.course.model.CourseCreateModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.entity.EntityController;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

//TODO manage access. Yes, I'll do it later

@RestController @RequestMapping("/course")
@RequiredArgsConstructor @Getter(AccessLevel.PROTECTED)
public class CourseController extends EntityController<CourseService, CourseModel, CourseCreateModel>
{
    private final CourseService service;

    @PreAuthorize("isAuthenticated() && hasAuthority('ADMIN')")
    @PostMapping("/create")
    @Override
    public @NotNull ResponseEntity<CourseModel> create(@NotNull @RequestBody CourseCreateModel model)
    {
        return super.create(model);
    }

    // TODO maybe!! make the admin privilege variable within the config
    @PreAuthorize("isAuthenticated() && hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{id}")
    @Override
    public @NotNull Boolean delete(@NotNull @PathVariable Long id)
    {
        return super.delete(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/get/{id}")
    @Override
    public @NotNull ResponseEntity<CourseModel> getData(@NotNull @PathVariable Long id)
    {
        return super.getData(id);
    }
}
