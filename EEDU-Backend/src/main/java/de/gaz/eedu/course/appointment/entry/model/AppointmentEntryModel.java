package de.gaz.eedu.course.appointment.entry.model;

import de.gaz.eedu.entity.model.Model;
import org.jetbrains.annotations.NotNull;

public record AppointmentEntryModel(
        long id,
        @NotNull
        String description,
        @NotNull String homework
) implements Model<Long> {}
