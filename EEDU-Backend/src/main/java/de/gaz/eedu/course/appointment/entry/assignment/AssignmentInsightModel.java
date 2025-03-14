package de.gaz.eedu.course.appointment.entry.assignment;

import de.gaz.eedu.course.appointment.entry.assignment.assessment.model.AssessmentModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record AssignmentInsightModel(
        @NotNull String name,
        boolean submitted,
        @NotNull String[] files,
        @Nullable AssessmentModel assessment
) {}
