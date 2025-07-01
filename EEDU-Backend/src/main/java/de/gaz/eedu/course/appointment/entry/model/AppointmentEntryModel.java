package de.gaz.eedu.course.appointment.entry.model;

import de.gaz.eedu.course.appointment.entry.assignment.AssignmentModel;
import de.gaz.eedu.course.room.model.RoomModel;
import de.gaz.eedu.entity.model.EntityModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record AppointmentEntryModel(
        @NotNull Long id,
        @Nullable Long attachedScheduled, // must not have one
        @Nullable RoomModel room, // must not have one
        @NotNull Long duration,
        @NotNull String description,
        @Nullable AssignmentModel assignment // must not have one or not published yet
) implements EntityModel<Long> {}