package de.gaz.eedu.course.appointment.entry.model;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public record AssignmentModel(@NotNull String description, @NotNull Long submitUntil)
{

    public AssignmentModel(@NotNull String description, @NotNull Instant submitUntil)
    {
        this(description, submitUntil.toEpochMilli());
    }
}
