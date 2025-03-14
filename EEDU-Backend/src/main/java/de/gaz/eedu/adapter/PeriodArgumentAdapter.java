package de.gaz.eedu.adapter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jetbrains.annotations.Nullable;

import java.time.Period;
import java.util.Objects;

@Converter(autoApply = true)
public class PeriodArgumentAdapter implements AttributeConverter<Period, Integer>
{
    @Override public @Nullable Integer convertToDatabaseColumn(@Nullable Period period)
    {
        if(Objects.isNull(period))
        {
            return null;
        }

        return period.getDays();
    }

    @Override public @Nullable Period convertToEntityAttribute(@Nullable Integer integer)
    {
        if(Objects.isNull(integer))
        {
            return null;
        }

        return Period.ofDays(integer);
    }
}
