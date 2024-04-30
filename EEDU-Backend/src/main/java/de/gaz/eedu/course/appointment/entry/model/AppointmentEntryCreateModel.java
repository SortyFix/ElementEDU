package de.gaz.eedu.course.appointment.entry.model;

import de.gaz.eedu.course.appointment.entry.AppointmentEntryEntity;
import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record AppointmentEntryCreateModel(@Nullable String description, @Nullable String homework) implements CreationModel<AppointmentEntryEntity>
{
    @Override public @NotNull AppointmentEntryEntity toEntity(@NotNull AppointmentEntryEntity entity)
    {
        entity.setDescription(description());
        entity.setHomework(homework());
        return entity;
    }
}
