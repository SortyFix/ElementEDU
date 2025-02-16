package de.gaz.eedu.course.appointment.entry.model;

import org.jetbrains.annotations.NotNull;

public record AssignmentInsightModel(@NotNull String user, boolean submitted, @NotNull String[] files) {}
