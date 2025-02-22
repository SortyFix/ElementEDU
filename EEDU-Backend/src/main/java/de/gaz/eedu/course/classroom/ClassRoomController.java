package de.gaz.eedu.course.classroom;

import de.gaz.eedu.course.CourseService;
import de.gaz.eedu.course.classroom.model.ClassRoomCreateModel;
import de.gaz.eedu.course.classroom.model.ClassRoomModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.user.UserService;
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

import java.util.Arrays;

import java.util.Set;

//TODO manage access

@RestController @RequestMapping("/api/v1/course/classroom") @RequiredArgsConstructor @Slf4j
public class ClassRoomController extends EntityController<Long, ClassRoomService, ClassRoomModel, ClassRoomCreateModel>
{
    @Getter(AccessLevel.PROTECTED) private final UserService userService;

    @Override protected @NotNull ClassRoomService getService()
    {
        return userService.getClassRoomService();
    }

    @PostMapping("{course}/link/{classroom}") public @NotNull HttpStatus linkClass(@PathVariable long course, @PathVariable long classroom)
    {
        log.info("Received incoming request for linking the class {} to course {}.", classroom, course);

        ClassRoomEntity classRoom = getService().loadEntityByIDSafe(classroom);
        CourseService courseService = getService().getCourseService();
        boolean modified = courseService.loadEntityByIDSafe(course).linkClassRoom(courseService, classRoom);
        return modified ? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
    }

    @PostMapping("{course}/unlink") public @NotNull HttpStatus unlinkClass(@PathVariable long course)
    {
        log.info("Received incoming request for unlinking the current class from course {}.", course);

        CourseService courseService = getService().getCourseService();
        boolean modified = courseService.loadEntityByIDSafe(course).unlinkClassRoom(courseService);
        return modified ? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
    }

    @GetMapping("/get/courses/{classroom}")
    public @NotNull ResponseEntity<CourseModel[]> getCourses(@AuthenticationPrincipal long user, @PathVariable long classroom)
    {
        return ResponseEntity.ok(getService().getCourses(user, classroom).toArray(new CourseModel[0]));
    }

    @PreAuthorize("hasAuthority('CLASS_CREATE')")
    @PostMapping("/create") @Override public @NotNull ResponseEntity<ClassRoomModel[]> create(@NotNull @RequestBody ClassRoomCreateModel[] model)
    {
        return super.create(model);
    }

    @PreAuthorize("hasAuthority('CLASS_DELETE')")
    @DeleteMapping("/delete/{id}") @Override public @NotNull ResponseEntity<Void> delete(@NotNull @PathVariable Long[] id)
    {
        return super.delete(id);
    }

    @PreAuthorize("@verificationService.isFullyAuthenticated()")
    @GetMapping("/get/{id}") @Override public @NotNull ResponseEntity<ClassRoomModel> getData(@NotNull @PathVariable Long id)
    {
        return super.getData(id);
    }

    @PreAuthorize("@verificationService.isFullyAuthenticated()")
    @GetMapping("/get/all")
    @Override public @NotNull ResponseEntity<Set<ClassRoomModel>> fetchAll()
    {
        return super.fetchAll();
    }
}
