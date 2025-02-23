package de.gaz.eedu.course.appointment.entry.model;

import de.gaz.eedu.course.appointment.entry.AppointmentEntryEntity;
import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Objects;

public record AppointmentEntryCreateModel(
        @NotNull Long start,
        @Nullable Long duration,
        @Nullable String room,
        @Nullable String description,
        @Nullable AssignmentCreateModel assignment
) implements CreationModel<Long, AppointmentEntryEntity>
{

    public AppointmentEntryCreateModel(@NotNull Long timeStamp)
    {
        this(timeStamp, null, null, null, null);
    }

    @Override
    public @NotNull AppointmentEntryEntity toEntity(@NotNull AppointmentEntryEntity entity) throws ResponseStatusException
    {
        // Duration is set inside the service

        entity.setDescription(description());
        entity.setPublish(Instant.now());

        if (Objects.nonNull(this.assignment()) && !this.assignment().assignValues(entity))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return entity;
    }
}
