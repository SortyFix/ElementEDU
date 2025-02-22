package de.gaz.eedu.course.appointment;

import de.gaz.eedu.course.appointment.entry.model.AppointmentEntryCreateModel;
import de.gaz.eedu.course.appointment.entry.model.AppointmentEntryModel;
import de.gaz.eedu.course.appointment.entry.model.AppointmentUpdateModel;
import de.gaz.eedu.course.appointment.entry.model.AssignmentInsightModel;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/{course}/schedule/frequent")
    @PreAuthorize("@verificationService.isFullyAuthenticated() && hasRole('teacher')")
    public @NotNull ResponseEntity<FrequentAppointmentModel[]> scheduleFrequentAppointment(@PathVariable long course, @NotNull @RequestBody FrequentAppointmentCreateModel... appointments)
    {
        log.info("Received incoming request for scheduling frequent appointment(s) {} in course {}.", appointments, course);

        Stream<FrequentAppointmentEntity> entities = getService().createEntity(Stream.of(appointments).map((model -> {
            return new InternalFrequentAppointmentCreateModel(course, model);
        })).collect(Collectors.toSet())).stream();

        return ResponseEntity.ok(entities.map(FrequentAppointmentEntity::toModel).toArray(FrequentAppointmentModel[]::new));
    }

    @PostMapping("/submit/assignment/{appointment}/status")
    @PreAuthorize("@verificationService.isFullyAuthenticated() && hasRole('teacher')")
    public @NotNull ResponseEntity<AssignmentInsightModel[]> submitStatus(@PathVariable long appointment)
    {
        return ResponseEntity.ok(getService().getInsight(appointment).toArray(AssignmentInsightModel[]::new));
    }

    @PostMapping("/submit/assignment/{appointment}/status/{user}")
    @PreAuthorize("@verificationService.isFullyAuthenticated() && hasRole('teacher')")
    public @NotNull ResponseEntity<AssignmentInsightModel> submitStatus(@PathVariable long appointment, @PathVariable long user)
    {
        ResponseEntity<AssignmentInsightModel> notFound = ResponseEntity.notFound().build();
        return getService().getInsight(appointment, user).map(ResponseEntity::ok).orElse(notFound);
    }

    @PostMapping("/submit/assignment/{appointment}")
    public @NotNull HttpStatus submitAssignment(@AuthenticationPrincipal long userId, @PathVariable long appointment, @NotNull @RequestPart("file") MultipartFile[] files)
    {
        getService().submitAssignment(userId, appointment, files);
        return HttpStatus.OK;
    }

    @PostMapping("/update/standalone/{appointment}")
    @PreAuthorize("hasRole('teacher') or hasRole('administrator')")
    public @NotNull ResponseEntity<AppointmentEntryModel> updateAppointment(@PathVariable long appointment, @NotNull @RequestBody AppointmentUpdateModel updateModel)
    {
        log.info("Received incoming request for updating the appointment {} with the updated data {}.", appointment, updateModel);
        return ResponseEntity.ok(getService().update(appointment, updateModel));
    }

    @PostMapping("/{course}/unschedule/frequent")
    @PreAuthorize("hasRole('teacher') or hasRole('administrator')")
    public @NotNull HttpStatus unscheduleAppointment(@PathVariable long course, @NotNull Long... appointments)
    {
        log.info("Received incoming request for unscheduling frequent appointment(s) {} from course {}.", appointments, course);
        boolean modified = getService().unscheduleFrequent(course, appointments);
        return modified ? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
    }

    @PostMapping("/{course}/schedule/standalone")
    @PreAuthorize("hasRole('teacher') or hasRole('administrator')")
    public @NotNull ResponseEntity<AppointmentEntryModel[]> setAppointment(@PathVariable long course, @RequestBody @NotNull AppointmentEntryCreateModel... createModel) {

        List<AppointmentEntryModel> createdEntities = getService().createAppointment(course,  Set.of(createModel));
        return ResponseEntity.ok(createdEntities.toArray(AppointmentEntryModel[]::new));
    }

    @DeleteMapping("/frequent/delete/{id}")
    @PreAuthorize("@verificationService.isFullyAuthenticated() && hasRole('teacher')")
    @Override public @NotNull HttpStatus delete(@PathVariable @NotNull Long... id)
    {
        return super.delete(id);
    }

    @GetMapping("/frequent/get/{id}")
    @Override public @NotNull ResponseEntity<FrequentAppointmentModel> getData(@PathVariable @NotNull Long id)
    {
        return super.getData(id);
    }

    @GetMapping("/frequent/get/all")
    @Override public @NotNull ResponseEntity<Set<FrequentAppointmentModel>> fetchAll()
    {
        return super.fetchAll();
    }
}
