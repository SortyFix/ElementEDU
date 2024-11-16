package de.gaz.eedu.course.appointment.entry.model;

import de.gaz.eedu.entity.model.EntityModel;
import org.jetbrains.annotations.NotNull;

public record AppointmentEntryModel(@NotNull Long id, @NotNull Long timeStamp, @NotNull String description, @NotNull String homework) implements EntityModel
{}
