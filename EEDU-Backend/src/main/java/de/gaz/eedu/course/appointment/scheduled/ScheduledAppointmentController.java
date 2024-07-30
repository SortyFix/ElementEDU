package de.gaz.eedu.course.appointment.scheduled;

import de.gaz.eedu.course.appointment.scheduled.model.ScheduledAppointmentCreateModel;
import de.gaz.eedu.course.appointment.scheduled.model.ScheduledAppointmentModel;
import de.gaz.eedu.entity.EntityController;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/course/appointment") @RequiredArgsConstructor @Getter(AccessLevel.PROTECTED)
public class ScheduledAppointmentController extends EntityController<ScheduledAppointmentService, ScheduledAppointmentModel, ScheduledAppointmentCreateModel>
{
    // TODO ?? What is this class about?
    private final ScheduledAppointmentService service;

    @PostMapping("/create") @Override public @NotNull ResponseEntity<ScheduledAppointmentModel> create(@RequestBody @NotNull ScheduledAppointmentCreateModel model)
    {
        return super.create(model);
    }

    @DeleteMapping("/delete/{id}") @Override public @NotNull Boolean delete(@PathVariable @NotNull Long id)
    {
        return super.delete(id);
    }

    @GetMapping("/get/{id}") @Override public @NotNull ResponseEntity<ScheduledAppointmentModel> getData(@PathVariable @NotNull Long id)
    {
        return super.getData(id);
    }
}
