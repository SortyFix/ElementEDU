package de.gaz.eedu.exception;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;

public class CreationException extends HTTPRequestException {

    public CreationException(@NotNull HttpStatus status)
    {
        super(status);
    }
}
