package de.gaz.eedu.course.classroom;

import de.gaz.eedu.course.classroom.model.ClassRoomCreateModel;
import de.gaz.eedu.course.classroom.model.ClassRoomModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.entity.EntityController;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
@RequestMapping("/api/v1/course/classroom")
public class ClassRoomController extends EntityController<String, ClassRoomService, ClassRoomModel, ClassRoomCreateModel>
{
    private final ClassRoomService service;

    @PostMapping("{course}/link/{classroom}")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).CLASS_LINK_COURSE.toString())")
    public @NotNull ResponseEntity<Void> linkClass(@PathVariable long course, @PathVariable String classroom)
    {
        log.info("Received incoming request for linking the class {} to course {}.", classroom, course);

        return empty(getService().linkClass(course, classroom) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
    }

    @PostMapping("{course}/unlink")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).CLASS_UNLINK_CLASS.toString())")
    public @NotNull ResponseEntity<Void> unlinkClass(@PathVariable long course)
    {
        log.info("Received incoming request for unlinking the current class from course {}.", course);
        return empty(getService().unlinkClass(course) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
    }

    @GetMapping("/get/courses/{classroom}")
    public @NotNull ResponseEntity<CourseModel[]> getCourses(@AuthenticationPrincipal long user, @PathVariable long classroom)
    {
        return ResponseEntity.ok(getService().getCourses(user, classroom).toArray(new CourseModel[0]));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).CLASS_CREATE.toString())")
    @Override public @NotNull ResponseEntity<ClassRoomModel[]> create(@NotNull @RequestBody ClassRoomCreateModel[] model)
    {
        return super.create(model);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).CLASS_DELETE.toString())")
    @Override public @NotNull ResponseEntity<Void> delete(@NotNull @PathVariable String[] id)
    {
        return super.delete(id);
    }
    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).CLASS_GET.toString())")
    @Override public @NotNull ResponseEntity<ClassRoomModel> getData(@NotNull @PathVariable String id)
    {
        return super.getData(id);
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority(T(de.gaz.eedu.user.privileges.SystemPrivileges).COURSE_GET.toString())")
    @Override public @NotNull ResponseEntity<Set<ClassRoomModel>> fetchAll()
    {
        return super.fetchAll();
    }

    @GetMapping("/get")
    @PreAuthorize("@verificationService.fullyAuthenticated()")
    public @NotNull ResponseEntity<ClassRoomModel[]> getOwn(@AuthenticationPrincipal long user)
    {
        return ResponseEntity.ok(getService().loadClassesByUser(user).toArray(new ClassRoomModel[0]));
    }
}
