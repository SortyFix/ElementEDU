package de.gaz.eedu.exception;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CreationException extends ResponseStatusException
{

    public CreationException(@NotNull HttpStatus status)
    {
        super(status);
    }
}
