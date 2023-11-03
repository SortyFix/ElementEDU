package de.gaz.eedu.user.exception;

import de.gaz.eedu.exception.CreationException;
import org.springframework.http.HttpStatus;

public class InsecurePasswordException extends CreationException {
    public InsecurePasswordException()
    {
        super(HttpStatus.NOT_ACCEPTABLE);
    }
}
