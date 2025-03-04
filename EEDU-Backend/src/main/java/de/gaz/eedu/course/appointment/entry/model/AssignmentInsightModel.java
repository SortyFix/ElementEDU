package de.gaz.eedu.course.appointment.entry.model;

import org.jetbrains.annotations.NotNull;

public record AssignmentInsightModel(@NotNull String name, boolean submitted, @NotNull String[] files) {}
