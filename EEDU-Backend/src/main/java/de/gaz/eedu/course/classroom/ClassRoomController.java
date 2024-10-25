package de.gaz.eedu.course.classroom;

import de.gaz.eedu.course.classroom.model.ClassRoomCreateModel;
import de.gaz.eedu.course.classroom.model.ClassRoomModel;
import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

//TODO manage access

@RestController @RequestMapping("/course/classroom") @RequiredArgsConstructor
public class ClassRoomController extends EntityController<ClassRoomService, ClassRoomModel, ClassRoomCreateModel>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassRoomEntity.class);

    @Getter(AccessLevel.PROTECTED) private final UserService userService;

    @Override protected @NotNull ClassRoomService getEntityService()
    {
        return userService.getClassRoomService();
    }

    @PostMapping("/attach/{classId}") public @NotNull HttpStatus attachUser(@NotNull @PathVariable Long classId, @NotNull @RequestBody Long... users)
    {
        LOGGER.info("Received incoming request for attaching user(s) {} from classroom {}.", users, classId);

        ClassRoomEntity classRoom = getEntityService().loadEntityByIDSafe(classId);
        UserEntity[] userArray = getUserService().loadEntityById(users).toArray(UserEntity[]::new);

        return classRoom.attachStudents(getEntityService(), userArray) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
    }

    @GetMapping("/detach/{classId}") public @NotNull HttpStatus detachUser(@NotNull @PathVariable Long classId, @NotNull @RequestBody Long... users)
    {
        LOGGER.info("Received incoming request for detaching user(s) {} from classroom {}.", users, classId);
        ClassRoomEntity classRoom = getEntityService().loadEntityByIDSafe(classId);
        return classRoom.detachStudents(getEntityService(), users) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
    }

    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/create") @Override public @NotNull ResponseEntity<ClassRoomModel> create(@NotNull @RequestBody ClassRoomCreateModel model) throws CreationException
    {
        return super.create(model);
    }

    @PreAuthorize("hasAuthority('ADMIN')") @DeleteMapping("/delete/{id}") @Override public @NotNull Boolean delete(@NotNull @PathVariable Long id)
    {
        return super.delete(id);
    }

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/{id}") @Override public @NotNull ResponseEntity<ClassRoomModel> getData(@NotNull @PathVariable Long id)
    {
        return super.getData(id);
    }
}
