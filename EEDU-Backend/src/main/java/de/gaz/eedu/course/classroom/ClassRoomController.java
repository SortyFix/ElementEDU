package de.gaz.eedu.course.classroom;

import de.gaz.eedu.course.model.ClassRoomCreateModel;
import de.gaz.eedu.course.model.ClassRoomModel;
import de.gaz.eedu.entity.EntityController;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

//TODO manage access

@RestController @RequestMapping("/course/classroom") @RequiredArgsConstructor
public class ClassRoomController extends EntityController<ClassRoomService, ClassRoomModel, ClassRoomCreateModel>
{
    private final ClassRoomService classRoomService;

    @Override protected @NotNull ClassRoomService getEntityService()
    {
        return classRoomService;
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
