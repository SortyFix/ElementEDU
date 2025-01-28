package de.gaz.eedu.course.appointment.entry.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record AssignmentInsightModel(@NotNull String user, boolean submitted, @Nullable String[] files) {}
