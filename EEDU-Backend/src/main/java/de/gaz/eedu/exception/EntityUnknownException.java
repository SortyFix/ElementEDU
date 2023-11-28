package de.gaz.eedu.exception;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

@Getter
public class EntityUnknownException extends HTTPRequestException
{
    private final long id;

    public EntityUnknownException(long id)
    {
        this(HttpStatus.NOT_FOUND, id);
    }

    public EntityUnknownException(@NotNull HttpStatus status, long id)
    {
        super(status);
        this.id = id;
    }
}
