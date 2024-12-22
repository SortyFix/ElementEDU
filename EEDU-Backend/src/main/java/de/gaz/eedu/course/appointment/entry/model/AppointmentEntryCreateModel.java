package de.gaz.eedu.course.appointment.entry.model;

import de.gaz.eedu.course.appointment.entry.AppointmentEntryEntity;
import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Objects;

public record AppointmentEntryCreateModel(@NotNull Long start, @Nullable Long duration, @Nullable String description,
                                          @Nullable AssignmentCreateModel homeWork) implements CreationModel<AppointmentEntryEntity>
{

    public AppointmentEntryCreateModel(@NotNull Long timeStamp)
    {
        this(timeStamp, null, null, null);
    }

    @Override public @NotNull AppointmentEntryEntity toEntity(@NotNull AppointmentEntryEntity entity) throws ResponseStatusException
    {
        entity.setStartTimeStamp(Instant.ofEpochSecond(start()));
        entity.setDescription(description());
        entity.setPublish(Instant.now());

        if(Objects.nonNull(this.homeWork()) && !this.homeWork().assignValues(entity))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return entity;
    }
}
