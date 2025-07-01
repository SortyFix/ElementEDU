package de.gaz.eedu.course.appointment.entry.assignment;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public record AssignmentModel(@NotNull String description, @NotNull Long publish, @NotNull Long submitUntil)
{
    public AssignmentModel(@NotNull String description, @NotNull Instant publish, @NotNull Instant submitUntil)
    {
        this(description, publish.toEpochMilli(), submitUntil.toEpochMilli());
    }
}
