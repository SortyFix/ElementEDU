package de.gaz.eedu.exception;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class CreationException extends HttpClientErrorException
{

    public CreationException(@NotNull HttpStatus status)
    {
        super(status);
    }
}
