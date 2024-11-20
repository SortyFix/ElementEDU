package de.gaz.eedu;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

@Converter(autoApply = true)
public class DurationArgumentAdapter implements AttributeConverter<Duration, Long>
{
    @Override
    public @Nullable Long convertToDatabaseColumn(@Nullable Duration duration)
    {
        if(Objects.isNull(duration))
        {
            return null;
        }
        return duration.toSeconds();
    }

    @Override
    public @Nullable Duration convertToEntityAttribute(@Nullable Long aLong)
    {
        if(Objects.isNull(aLong))
        {
            return null;
        }
        return Duration.ofSeconds(aLong);
    }
}
