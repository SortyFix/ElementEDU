package de.gaz.eedu.course.appointment.entry.assignment;

import de.gaz.eedu.course.appointment.entry.assignment.assessment.model.AssessmentModel;
import de.gaz.eedu.user.model.ReducedUserModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record AssignmentInsightModel(
        @NotNull ReducedUserModel user,
        boolean submitted,
        @NotNull String[] files,
        @Nullable AssessmentModel assessment
) {}
