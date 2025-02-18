package de.gaz.eedu.course.classroom;

import de.gaz.eedu.course.CourseService;
import de.gaz.eedu.course.classroom.model.ClassRoomCreateModel;
import de.gaz.eedu.course.classroom.model.ClassRoomModel;
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
import org.springframework.web.bind.annotation.*;

import java.util.Set;

//TODO manage access

@RestController @RequestMapping("/api/v1/course/classroom") @RequiredArgsConstructor @Slf4j
public class ClassRoomController extends EntityController<ClassRoomService, ClassRoomModel, ClassRoomCreateModel>
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

    @PreAuthorize("hasAuthority('CLASS_CREATE')")
    @PostMapping("/create") @Override public @NotNull ResponseEntity<ClassRoomModel[]> create(@NotNull @RequestBody ClassRoomCreateModel[] model)
    {
        return super.create(model);
    }

    @PreAuthorize("hasAuthority('CLASS_DELETE')")
    @DeleteMapping("/delete/{id}") @Override public @NotNull HttpStatus delete(@NotNull @PathVariable Long... id)
    {
        return super.delete(id);
    }

    @PreAuthorize("@verificationService.hasToken(T(de.gaz.eedu.user.verification.JwtTokenType).AUTHORIZED)")
    @GetMapping("/get/{id}") @Override public @NotNull ResponseEntity<ClassRoomModel> getData(@NotNull @PathVariable Long id)
    {
        return super.getData(id);
    }

    @PreAuthorize("@verificationService.hasToken(T(de.gaz.eedu.user.verification.JwtTokenType).AUTHORIZED)")
    @GetMapping("/get/all")
    @Override public @NotNull ResponseEntity<Set<ClassRoomModel>> fetchAll()
    {
        return super.fetchAll();
    }
}
