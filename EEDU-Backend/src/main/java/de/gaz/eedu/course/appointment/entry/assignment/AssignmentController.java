package de.gaz.eedu.course.appointment.entry.assignment;

import de.gaz.eedu.course.appointment.AppointmentService;
import de.gaz.eedu.course.appointment.entry.assignment.assessment.AssessmentService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/course/appointment/assignment")
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class AssignmentController
{
    private final AssessmentService service;
    private final AppointmentService appointmentService;

    @PreAuthorize("hasRole('teacher')") @GetMapping("/{appointment}/status/all")
    public @NotNull ResponseEntity<AssignmentInsightModel[]> submitStatus(@PathVariable long appointment)
    {
        return ResponseEntity.ok(getAppointmentService().getInsight(appointment).toArray(AssignmentInsightModel[]::new));
    }

    @PreAuthorize("hasRole('teacher')") @GetMapping("/{appointment}/status/{user}")
    public @NotNull ResponseEntity<AssignmentInsightModel> submitStatus(@PathVariable long appointment, @PathVariable long user)
    {
        ResponseEntity<AssignmentInsightModel> notFound = ResponseEntity.notFound().build();
        return getAppointmentService().getInsight(appointment, user).map(ResponseEntity::ok).orElse(notFound);
    }

    @PreAuthorize("hasRole('student')") @GetMapping("/{appointment}/status")
    public @NotNull ResponseEntity<AssignmentInsightModel> ownSubmitStatus(@AuthenticationPrincipal long userId, @PathVariable long appointment)
    {
        ResponseEntity<AssignmentInsightModel> notFound = ResponseEntity.notFound().build();
        return getAppointmentService().getInsight(appointment, userId).map(ResponseEntity::ok).orElse(notFound);
    }

    @DeleteMapping("/{appointment}/delete/{files}")
    public @NotNull ResponseEntity<Void> deleteAssignment(@AuthenticationPrincipal long userId, @PathVariable long appointment, @PathVariable @NotNull String[] files)
    {
        return ResponseEntity.status(getAppointmentService().deleteAssignment(
                userId,
                appointment,
                files) ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR
        ).build();
    }

    @PostMapping("/{appointment}/submit")
    public @NotNull ResponseEntity<Void> submitAssignment(@AuthenticationPrincipal long userId, @PathVariable long appointment, @NotNull @RequestPart(
            "file"
    ) MultipartFile[] files)
    {
        getAppointmentService().submitAssignment(userId, appointment, files);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
