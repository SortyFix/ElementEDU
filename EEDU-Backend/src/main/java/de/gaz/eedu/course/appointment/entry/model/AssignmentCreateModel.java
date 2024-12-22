package de.gaz.eedu.course.appointment.entry.model;

import de.gaz.eedu.course.appointment.entry.AppointmentEntryEntity;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public record AssignmentCreateModel(@NotNull String description, @NotNull Long submitUntil, @NotNull Long publish)
{

    public boolean assignValues(@NotNull AppointmentEntryEntity entity)
    {
        return entity.setAssignment(this);
    }

    public boolean validate(long startTimeStamp)
    {
        return startTimeStamp < this.submitUntil()  // submit after appointment start
                && startTimeStamp <= this.publish()  // publish after or at appointment start
                && this.publish() < this.submitUntil(); // publish before deadline
    }

    public @NotNull Instant submitUntilInstant()
    {
        return Instant.ofEpochMilli(submitUntil);
    }

    public @NotNull Instant publishInstant()
    {
        return Instant.ofEpochMilli(publish);
    }
}
