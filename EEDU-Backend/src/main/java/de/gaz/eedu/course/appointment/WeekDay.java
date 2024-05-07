package de.gaz.eedu.course.appointment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WeekDay
{
    MONDAY((byte) 0), TUESDAY((byte) 1), WEDNESDAY((byte) 2), THURSDAY((byte) 3), FRIDAY((byte) 4);

    private final byte value;
}
