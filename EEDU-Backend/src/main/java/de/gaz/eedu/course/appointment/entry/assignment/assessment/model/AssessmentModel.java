package de.gaz.eedu.course.appointment.entry.assignment.assessment.model;

import de.gaz.eedu.entity.model.EntityModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record AssessmentModel(@NotNull Long id, @Nullable String feedback) implements EntityModel<Long> {}
