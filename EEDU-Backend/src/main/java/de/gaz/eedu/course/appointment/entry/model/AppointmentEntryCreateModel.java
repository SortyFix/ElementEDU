package de.gaz.eedu.course.appointment.entry.model;

import de.gaz.eedu.adapter.InstantArgumentAdapter;
import de.gaz.eedu.course.appointment.entry.AppointmentEntryEntity;
import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Objects;

public record AppointmentEntryCreateModel(@NotNull Long timeStamp, @Nullable Long duration, @Nullable String description,
                                          @Nullable String homework, @Nullable Boolean submitHomework,
                                          @Nullable Long submitUntil) implements CreationModel<AppointmentEntryEntity>
{

    public AppointmentEntryCreateModel(@NotNull Long timeStamp)
    {
        this(timeStamp, null, null, null, false, null);
    }

    @Override public @NotNull AppointmentEntryEntity toEntity(@NotNull AppointmentEntryEntity entity) throws ResponseStatusException
    {
        entity.setTimeStamp(Instant.ofEpochSecond(timeStamp()));
        entity.setDescription(description());
        entity.setHomework(homework());
        entity.setSubmitHomework(submitHomework());
        validateSubmitUntil();
        entity.setSubmitUntil(getSubmitUntil());
        entity.setPublish(Instant.now());
        return entity;
    }

    private void validateSubmitUntil() throws ResponseStatusException
    {
        if (!submitHomework() && Objects.nonNull(submitUntil()))
        {
            String errorMessage = "SubmitUntil can only be set, when submitHomework is true.";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        if (Objects.nonNull(submitUntil()) && timeStamp() > submitUntil())
        {
            String errorMessage = "Attribute submitUntil must be greater than attribute timeStamp.";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }

    @Override public @NotNull Boolean submitHomework()
    {
        return Objects.requireNonNullElse(submitHomework, false);
    }

    public @Nullable Instant getSubmitUntil()
    {
        InstantArgumentAdapter adapter = new InstantArgumentAdapter();
        return adapter.convertToEntityAttribute(submitUntil());
    }
}
