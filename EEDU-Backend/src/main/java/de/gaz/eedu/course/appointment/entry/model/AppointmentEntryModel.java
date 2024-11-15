package de.gaz.eedu.course.appointment.entry.model;

import org.jetbrains.annotations.NotNull;

public record AppointmentEntryModel(long id, @NotNull String description, @NotNull String homework)
{}
