package de.gaz.eedu.course;

import de.gaz.eedu.course.model.CourseCreateModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.user.model.ReducedUserModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

//TODO manage access. Yes, I'll do it later

@Slf4j
@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class CourseController extends EntityController<Long, CourseService, CourseModel, CourseCreateModel>
{
    private final CourseService service;

    @GetMapping("/{course}/subject/{subject}")
    public @NotNull ResponseEntity<Void> setSubject(@PathVariable long course, @PathVariable String subject)
    {
        log.info("Received incoming request for setting the subject of course {} to {}.", course, subject);
        return empty(getService().setSubject(course, subject) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/{course}/attach")
    public @NotNull ResponseEntity<Void> attachUser(@PathVariable long course, @NotNull @RequestBody Long... users)
    {
        log.info("Received incoming request for attaching user(s) {} to course {}.", users, course);
        return empty(getService().attachUser(course, users) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping("{course}/detach")
    public @NotNull ResponseEntity<Void> detachUser(@PathVariable long course, @NotNull @RequestBody Long... users)
    {
        log.info("Received incoming request for detaching user(s) {} from course {}.", users, course);
        return empty(getService().detachUser(course, users) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).COURSE_CREATE.toString())")
    @Override public @NotNull ResponseEntity<CourseModel[]> create(@NotNull @RequestBody CourseCreateModel[] model)
    {
        return super.create(model);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).COURSE_DELETE.toString())")
    @Override public @NotNull ResponseEntity<Void> delete(@NotNull @PathVariable Long[] id)
    {
        return super.delete(id);
    }

    @GetMapping("/get/{id}")
    @Override public @NotNull ResponseEntity<CourseModel> getData(@NotNull @PathVariable Long id)
    {
        return super.getData(id);
    }

    @GetMapping("/get/courses/{user}")
    public @NotNull ResponseEntity<CourseModel[]> getCourses(@PathVariable long user)
    {
        return ResponseEntity.ok(getService().getCourses(user));
    }

    @GetMapping("/get")
    public @NotNull ResponseEntity<CourseModel[]> getOwnCourses(@AuthenticationPrincipal long user)
    {
        return getCourses(user);
    }

    @GetMapping("/get/all")
    @Override public @NotNull ResponseEntity<Set<CourseModel>> fetchAll()
    {
        return super.fetchAll();
    }

    @GetMapping("/users/{course}")
    public @NotNull ResponseEntity<ReducedUserModel[]> getUsers(@PathVariable long course)
    {
        return ResponseEntity.ok(getService().loadReducedModelsByCourse(course).toArray(new ReducedUserModel[0]));
    }
}
