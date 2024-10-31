package de.gaz.eedu;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;

@Converter(autoApply = true)
public class InstantArgumentAdapter implements AttributeConverter<Instant, Long>
{
    @Override
    public @Nullable Long convertToDatabaseColumn(@Nullable Instant instant)
    {
        if(Objects.isNull(instant))
        {
            return null;
        }
        return instant.getEpochSecond();
    }

    @Override
    public @Nullable Instant convertToEntityAttribute(@Nullable Long aLong)
    {
        if(Objects.isNull(aLong))
        {
            return null;
        }
        return Instant.ofEpochSecond(aLong);
    }
}
