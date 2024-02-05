package de.gaz.eedu.course.classroom;

import de.gaz.eedu.course.classroom.model.ClassRoomCreateModel;
import de.gaz.eedu.course.classroom.model.ClassRoomModel;
import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

//TODO manage access

@RestController @RequestMapping("/course/classroom") @RequiredArgsConstructor
public class ClassRoomController extends EntityController<ClassRoomService, ClassRoomModel, ClassRoomCreateModel>
{
    @Getter(AccessLevel.PROTECTED) private final UserService userService;

    @Override protected @NotNull ClassRoomService getEntityService()
    {
        return userService.getClassRoomService();
    }

    @GetMapping("/attach/{userId}/{classId}")
    public @NotNull HttpStatus attachUser(@NotNull @PathVariable Long userId, @NotNull @PathVariable Long classId)
    {
        UserEntity user = getUserService().loadEntityByIDSafe(userId);
        if(getEntityService().loadEntityByIDSafe(classId).attachStudents(getEntityService(), user))
        {
            return HttpStatus.OK;
        }
        return HttpStatus.CONFLICT;
    }

    @GetMapping("/detach/{userId}/{classId}")
    public @NotNull HttpStatus detachUser(@NotNull @PathVariable Long userId, @NotNull @PathVariable Long classId)
    {
        if(getEntityService().loadEntityByIDSafe(classId).detachStudents(userId))
        {
            return HttpStatus.OK;
        }
        return HttpStatus.NOT_FOUND;
    }

    @PreAuthorize("hasAuthority('ADMIN')") @PostMapping("/create") @Override
    public @NotNull ResponseEntity<ClassRoomModel> create(@NotNull @RequestBody ClassRoomCreateModel model)
    {
        return super.create(model);
    }

    @PreAuthorize("hasAuthority('ADMIN')") @DeleteMapping("/delete/{id}") @Override
    public @NotNull Boolean delete(@NotNull @PathVariable Long id)
    {
        return super.delete(id);
    }

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/{id}") @Override
    public @NotNull ResponseEntity<ClassRoomModel> getData(@NotNull @PathVariable Long id)
    {
        return super.getData(id);
    }
}
