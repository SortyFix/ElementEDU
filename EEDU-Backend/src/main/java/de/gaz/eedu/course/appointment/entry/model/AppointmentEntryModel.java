package de.gaz.eedu.course.appointment.entry.model;

import de.gaz.eedu.entity.model.EntityModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record AppointmentEntryModel(@NotNull Long id, @Nullable Long attachedScheduled, @NotNull Long start, @NotNull Long duration, @NotNull String description, @NotNull String homework) implements EntityModel
{}
