package de.gaz.eedu.user.verification.authority;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class InvalidTokenException extends HttpClientErrorException
{
    public InvalidTokenException()
    {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
