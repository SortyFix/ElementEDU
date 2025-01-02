package de.gaz.eedu.course.appointment.frequent;

import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.appointment.frequent.model.InternalFrequentAppointmentCreateModel;
import de.gaz.eedu.course.appointment.frequent.model.ScheduledAppointmentCreateModel;
import de.gaz.eedu.course.appointment.frequent.model.ScheduledAppointmentModel;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j @RestController @RequestMapping("/api/v1/course/appointment") @RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class FrequentAppointmentController extends EntityController<FrequentAppointmentService, ScheduledAppointmentModel, InternalFrequentAppointmentCreateModel>
{
    // TODO ?? What is this class about?
    private final FrequentAppointmentService service;


    @PostMapping("/{course}/schedule/frequent") public @NotNull ResponseEntity<ScheduledAppointmentModel[]> scheduleFrequentAppointment(@PathVariable long course, @NotNull @RequestBody ScheduledAppointmentCreateModel... appointments)
    {
        log.info("Received incoming request for scheduling frequent appointment(s) {} in course {}.", appointments, course);

        Stream<FrequentAppointmentEntity> entities = getService().createEntity(Stream.of(appointments).map((model -> {
            return new InternalFrequentAppointmentCreateModel(course, model);
        })).collect(Collectors.toSet())).stream();

        return ResponseEntity.ok(entities.map(FrequentAppointmentEntity::toModel).toArray(ScheduledAppointmentModel[]::new));
    }

    @PostMapping("/{course}/unschedule/frequent") public @NotNull HttpStatus unscheduleAppointment(@PathVariable long course, @NotNull Long... appointments)
    {
        log.info("Received incoming request for unscheduling frequent appointment(s) {} from course {}.", appointments, course);

        CourseEntity courseEntity = getService().getCourseService().loadEntityByIDSafe(course);
        boolean modified = courseEntity.unscheduleFrequent(getService().getCourseService(), appointments);
        return modified ? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
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
