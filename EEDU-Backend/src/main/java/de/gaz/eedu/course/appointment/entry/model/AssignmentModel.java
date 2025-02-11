package de.gaz.eedu.course.appointment.entry.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;

public record AssignmentModel(@NotNull String description, @Nullable Long publish, @NotNull Long submitUntil)
{

    public AssignmentModel(@NotNull String description, @Nullable Instant publish, @NotNull Instant submitUntil)
    {
        this(description, Objects.isNull(publish) ? null : publish.toEpochMilli(), submitUntil.toEpochMilli());
    }
}
