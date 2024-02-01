package de.gaz.eedu.exception;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class EntityUnknownException extends ResponseStatusException
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
