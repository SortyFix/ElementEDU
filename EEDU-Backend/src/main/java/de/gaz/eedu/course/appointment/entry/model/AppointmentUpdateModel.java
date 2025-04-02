package de.gaz.eedu.course.appointment.entry.model;

import de.gaz.eedu.course.appointment.entry.assignment.AssignmentCreateModel;
import org.jetbrains.annotations.Nullable;

public record AppointmentUpdateModel(
        @Nullable String room,
        @Nullable String description,
        @Nullable AssignmentCreateModel assignment
) {}
