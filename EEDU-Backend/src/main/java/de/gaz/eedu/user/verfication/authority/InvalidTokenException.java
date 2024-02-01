package de.gaz.eedu.user.verfication.authority;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class InvalidTokenException extends HttpClientErrorException
{
    public InvalidTokenException()
    {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
