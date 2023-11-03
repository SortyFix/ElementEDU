package de.gaz.eedu.exception;

import org.springframework.http.HttpStatus;

public class OccupiedException extends CreationException {
    public OccupiedException()
    {
        super(HttpStatus.CONFLICT);
    }
}
