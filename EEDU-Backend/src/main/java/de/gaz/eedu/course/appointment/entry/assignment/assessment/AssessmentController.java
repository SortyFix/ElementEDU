package de.gaz.eedu.course.appointment.entry.assignment.assessment;

import de.gaz.eedu.course.appointment.entry.assignment.assessment.model.AssessmentCreateModel;
import de.gaz.eedu.course.appointment.entry.assignment.assessment.model.AssessmentModel;
import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.exception.CreationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/course/appointment/assignment/assessment")
@RequiredArgsConstructor @Getter(AccessLevel.PROTECTED)
public class AssessmentController extends EntityController<Long, AssessmentService, AssessmentModel, AssessmentCreateModel>
{
    private final AssessmentService service;

    @PreAuthorize("hasRole('teacher')")
    @PutMapping("/{assessment}/set/feedback/{feedback}")
    public @NotNull ResponseEntity<AssessmentModel> setFeedback(@PathVariable long assessment, @PathVariable @NotNull String feedback)
    {
        return ResponseEntity.ok(getService().setFeedback(assessment, feedback));
    }

    @PreAuthorize("hasRole('teacher')")
    @PutMapping("/{assessment}/unset/feedback")
    public @NotNull ResponseEntity<AssessmentModel> unsetFeedback(@PathVariable long assessment)
    {
        return ResponseEntity.ok(getService().setFeedback(assessment, null));
    }

    @PreAuthorize("hasRole('teacher')")
    @PutMapping("/{assessment}/set/grade/{grade}")
    public @NotNull ResponseEntity<AssessmentModel> setGrade(@PathVariable long assessment, @PathVariable float grade)
    {
        return ResponseEntity.ok(getService().setGrade(assessment, grade));
    }

    @PreAuthorize("hasRole('teacher')")
    @PutMapping("/{assessment}/unset/grade")
    public @NotNull ResponseEntity<AssessmentModel> unsetGrade(@PathVariable long assessment)
    {
        return ResponseEntity.ok(getService().setGrade(assessment, null));
    }

    @PreAuthorize("hasRole('teacher')")
    @Override @PostMapping("/create")
    public @NotNull ResponseEntity<AssessmentModel[]> create(@NotNull @RequestBody AssessmentCreateModel[] model) throws CreationException
    {
        return super.create(model);
    }

    @PreAuthorize("hasRole('teacher')")
    @DeleteMapping("/delete/{id}") @Override
    public @NotNull ResponseEntity<Void> delete(@NotNull @PathVariable Long[] id)
    {
        return super.delete(id);
    }

    @PreAuthorize("hasRole('student')")
    @GetMapping("/get/{appointment}")
    public @NotNull ResponseEntity<AssessmentModel> getOwnData(@NotNull @PathVariable Long appointment, @AuthenticationPrincipal long user)
    {
        return getData(appointment, user);
    }

    @PreAuthorize("hasRole('teacher')")
    @GetMapping("/get/{appointment}/{user}")
    public @NotNull ResponseEntity<AssessmentModel> getData(@NotNull @PathVariable Long appointment , @PathVariable long user)
    {
        return super.getData(getService().getId(user, appointment));
    }
}
