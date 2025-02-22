package de.gaz.eedu.course;

import de.gaz.eedu.course.model.CourseCreateModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.user.UserEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

//TODO manage access. Yes, I'll do it later

@Slf4j @RestController @RequestMapping("/api/v1/course") @RequiredArgsConstructor @Getter(AccessLevel.PROTECTED)
public class CourseController extends EntityController<Long, CourseService, CourseModel, CourseCreateModel>
{
    private final CourseService service;

    @GetMapping("/{course}/subject/{subject}") public void setSubject(@PathVariable long course, @PathVariable long subject)
    {
        CourseEntity courseEntity = getService().loadEntityByIDSafe(course);
        courseEntity.setSubject(getService(), getService().getSubjectService().loadEntityByIDSafe(subject));
    }

    @PostMapping("/{course}/attach") public @NotNull HttpStatus attachUser(@PathVariable long course, @NotNull @RequestBody Long... users)
    {
        log.info("Received incoming request for attaching user(s) {} to course {}.", users, course);

        UserEntity[] entities = getService().getUserRepository().findAllById(Set.of(users)).toArray(UserEntity[]::new);
        boolean modified = getService().loadEntityByIDSafe(course).attachUsers(getService(), entities);
        return modified ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
    }

    @GetMapping("{course}/detach") public @NotNull HttpStatus detachUser(@PathVariable long course, @NotNull @RequestBody Long... users)
    {
        log.info("Received incoming request for detaching user(s) {} from course {}.", users, course);

        boolean modified = getService().loadEntityByIDSafe(course).detachUsers(getService(), users);
        return modified ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
    }

    @PostMapping("/create") @Override public @NotNull ResponseEntity<CourseModel[]> create(@NotNull @RequestBody CourseCreateModel[] model)
    {
        return super.create(model);
    }

    @DeleteMapping("/delete/{id}") @Override public @NotNull Boolean delete(@NotNull @PathVariable Long id)
    {
        return super.delete(id);
    }

    @GetMapping("/get/{id}") @Override public @NotNull ResponseEntity<CourseModel> getData(@NotNull @PathVariable Long id)
    {
        return super.getData(id);
    }
}
