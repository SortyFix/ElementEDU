package de.gaz.eedu.course.appointment;

import de.gaz.eedu.course.appointment.model.CourseAppointmentCreateModel;
import de.gaz.eedu.course.appointment.model.CourseAppointmentModel;
import de.gaz.eedu.entity.EntityController;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/course/appointment") @RequiredArgsConstructor @Getter(AccessLevel.PROTECTED)
public class CourseAppointmentController extends EntityController<CourseAppointmentService, CourseAppointmentModel, CourseAppointmentCreateModel>
{
    private final CourseAppointmentService service;

    @PostMapping("/create") @Override public @NotNull ResponseEntity<CourseAppointmentModel> create(@RequestBody @NotNull CourseAppointmentCreateModel model)
    {
        return super.create(model);
    }

    @DeleteMapping("/delete/{id}") @Override public @NotNull Boolean delete(@PathVariable @NotNull Long id)
    {
        return super.delete(id);
    }

    @GetMapping("/get/{id}") @Override public @NotNull ResponseEntity<CourseAppointmentModel> getData(@PathVariable @NotNull Long id)
    {
        return super.getData(id);
    }
}
