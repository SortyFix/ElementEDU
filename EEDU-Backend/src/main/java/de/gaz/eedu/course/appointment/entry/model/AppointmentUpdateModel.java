package de.gaz.eedu.course.appointment.entry.model;

import org.jetbrains.annotations.Nullable;

public record AppointmentUpdateModel(@Nullable Long room, @Nullable String description, @Nullable AssignmentCreateModel assignment) {}
