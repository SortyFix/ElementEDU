package de.gaz.eedu.course.appointment;

import de.gaz.eedu.course.appointment.entry.assignment.AssignmentCreateModel;
import de.gaz.eedu.course.appointment.entry.model.AppointmentEntryCreateModel;
import de.gaz.eedu.course.appointment.entry.model.AppointmentEntryModel;
import de.gaz.eedu.course.appointment.frequent.FrequentAppointmentEntity;
import de.gaz.eedu.course.appointment.frequent.model.FrequentAppointmentCreateModel;
import de.gaz.eedu.course.appointment.frequent.model.FrequentAppointmentModel;
import de.gaz.eedu.course.appointment.frequent.model.InternalFrequentAppointmentCreateModel;
import de.gaz.eedu.entity.EntityController;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/api/v1/course/appointment")
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class AppointmentController extends EntityController<Long, AppointmentService, FrequentAppointmentModel, InternalFrequentAppointmentCreateModel>
{
    private final AppointmentService service;

    private static @NotNull Stream<InternalFrequentAppointmentCreateModel> toInternalCreateModel(long course, @NotNull FrequentAppointmentCreateModel[] appointments)
    {
        return Stream.of(appointments).map((model -> new InternalFrequentAppointmentCreateModel(course, model)));
    }

    @PostMapping("/{course}/schedule/frequent")
    @PreAuthorize("hasRole('teacher')")
    public @NotNull ResponseEntity<FrequentAppointmentModel[]> scheduleFrequentAppointment(@PathVariable long course, @NotNull @RequestBody FrequentAppointmentCreateModel... appointments)
    {
        String message = "Received incoming request for scheduling frequent appointment(s) {} in course {}.";
        log.info(message, appointments, course);

        Set<InternalFrequentAppointmentCreateModel> internal = toInternalCreateModel(course, appointments).collect(Collectors.toUnmodifiableSet());
        Stream<FrequentAppointmentEntity> entities = getService().createEntity(internal).stream();
        return ResponseEntity.ok(entities.map(FrequentAppointmentEntity::toModel).toArray(FrequentAppointmentModel[]::new));
    }

    @PutMapping("/update/standalone/{appointment}/set/description/{description}") @PreAuthorize("hasRole('teacher') or hasRole('administrator')")
    public @NotNull ResponseEntity<Void> setDescription(@PathVariable long appointment, @NotNull @PathVariable String description)
    {
        String message = "Received incoming request for altering the appointment {} with the updated description {}.";
        log.info(message, appointment, description);
        return empty(getService().setDescription(appointment, description) ? HttpStatus.OK : HttpStatus.CONFLICT);
    }

    @DeleteMapping("/update/standalone/{appointment}/unset/description")
    @PreAuthorize("hasRole('teacher') or hasRole('administrator')")
    public @NotNull ResponseEntity<Void> unsetDescription(@PathVariable long appointment)
    {
        log.info("Received incoming request for altering the appointment {} by unsetting description.", appointment);
        return empty(getService().unsetRoom(appointment) ? HttpStatus.OK : HttpStatus.CONFLICT);
    }

    @PutMapping("/update/standalone/{appointment}/set/room/{room}")
    @PreAuthorize("hasRole('teacher') or hasRole('administrator')")
    public @NotNull ResponseEntity<Void> setRoom(@PathVariable long appointment, @NotNull @PathVariable String room)
    {
        log.info("Received incoming request for altering the appointment {} with the updated room {}.", appointment, room);
        return empty(getService().setRoom(appointment, room) ? HttpStatus.OK : HttpStatus.CONFLICT);
    }

    @DeleteMapping("/update/standalone/{appointment}/unset/room")
    @PreAuthorize("hasRole('teacher') or hasRole('administrator')")
    public @NotNull ResponseEntity<Void> unsetRoom(@PathVariable long appointment)
    {
        log.info("Received incoming request for altering the appointment {} by unsetting room.", appointment);
        return empty(getService().unsetRoom(appointment) ? HttpStatus.OK : HttpStatus.CONFLICT);
    }

    @PutMapping("/update/standalone/{appointment}/set/assignment")
    @PreAuthorize("hasRole('teacher') or hasRole('administrator')")
    public @NotNull ResponseEntity<Void> setAssignment(@PathVariable long appointment, @NotNull AssignmentCreateModel assignment)
    {
        log.info("Received incoming request for altering the appointment {} with the updated assignment {}.", appointment, appointment);
        return empty(getService().setAssignment(appointment, assignment) ? HttpStatus.OK : HttpStatus.CONFLICT);
    }

    @PostMapping("/update/standalone/{appointment}/unset/assignment")
    @PreAuthorize("hasRole('teacher') or hasRole('administrator')")
    public @NotNull ResponseEntity<Void> unsetAssignment(@PathVariable long appointment)
    {
        log.info("Received incoming request for altering the appointment {} by unsetting assignment.", appointment);
        return empty(getService().unsetAssignment(appointment) ? HttpStatus.OK : HttpStatus.CONFLICT);
    }

    @PostMapping("/{course}/unschedule/frequent") @PreAuthorize("hasRole('teacher') or hasRole('administrator')")
    public @NotNull ResponseEntity<Void> unscheduleAppointment(@PathVariable long course, @NotNull Long... appointments)
    {
        log.info("Received incoming request for unscheduling frequent appointment(s) {} from course {}.", appointments, course);
        boolean modified = getService().unscheduleFrequent(course, appointments);
        return empty(modified ? HttpStatus.OK : HttpStatus.CONFLICT);
    }

    @PostMapping("/{course}/schedule/standalone") @PreAuthorize("hasRole('teacher') or hasRole('administrator')")
    public @NotNull ResponseEntity<AppointmentEntryModel[]> setAppointment(@PathVariable long course, @RequestBody @NotNull AppointmentEntryCreateModel... createModel)
    {

        List<AppointmentEntryModel> createdEntities = getService().createAppointment(course, Set.of(createModel));
        return ResponseEntity.ok(createdEntities.toArray(AppointmentEntryModel[]::new));
    }

    @DeleteMapping("/frequent/delete/{id}")
    @PreAuthorize("@verificationService.isFullyAuthenticated() && hasRole('teacher')") @Override
    public @NotNull ResponseEntity<Void> delete(@PathVariable @NotNull Long[] id)
    {
        return super.delete(id);
    }

    @GetMapping("/frequent/get/{id}") @Override
    public @NotNull ResponseEntity<FrequentAppointmentModel> getData(@PathVariable @NotNull Long id)
    {
        return super.getData(id);
    }

    @GetMapping("/frequent/get/all") @Override public @NotNull ResponseEntity<Set<FrequentAppointmentModel>> fetchAll()
    {
        return super.fetchAll();
    }
}
