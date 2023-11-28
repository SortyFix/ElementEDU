package de.gaz.eedu.user.verfication;

import de.gaz.eedu.exception.HTTPRequestException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends HTTPRequestException
{
    public InvalidTokenException()
    {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
