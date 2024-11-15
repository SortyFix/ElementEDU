package de.gaz.eedu.course.appointment.scheduled;

import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.appointment.scheduled.model.ScheduledAppointmentCreateModel;
import de.gaz.eedu.course.appointment.scheduled.model.ScheduledAppointmentModel;
import de.gaz.eedu.entity.EntityController;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j @RestController @RequestMapping("/api/v1/course/appointment") @RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class ScheduledAppointmentController extends EntityController<ScheduledAppointmentService, ScheduledAppointmentModel, ScheduledAppointmentCreateModel>
{
    // TODO ?? What is this class about?
    private final ScheduledAppointmentService service;


    @PostMapping("/{course}/schedule") public @NotNull HttpStatus scheduleAppointment(@PathVariable long course, @NotNull Long... appointments)
    {
        log.info("Received incoming request for scheduling appointment(s) {} in course {}.", appointments, course);

        CourseEntity courseEntity = getService().getCourseService().loadEntityByIDSafe(course);
        ScheduledAppointmentEntity[] entities = getService().loadEntityById(appointments)
                                                            .toArray(ScheduledAppointmentEntity[]::new);
        boolean modified = courseEntity.scheduleRepeating(getService().getCourseService(), entities);
        return modified ? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
    }

    @PostMapping("/{course}/unschedule") public @NotNull HttpStatus unscheduleAppointment(@PathVariable long course, @NotNull Long... appointments)
    {
        log.info("Received incoming request for unscheduling appointment(s) {} from course {}.", appointments, course);

        CourseEntity courseEntity = getService().getCourseService().loadEntityByIDSafe(course);
        boolean modified = courseEntity.unscheduleRepeating(getService().getCourseService(), appointments);
        return modified ? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
    }

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

    @GetMapping("/get/all")
    @Override public @NotNull ResponseEntity<Set<ScheduledAppointmentModel>> fetchAll()
    {
        return super.fetchAll();
    }
}
