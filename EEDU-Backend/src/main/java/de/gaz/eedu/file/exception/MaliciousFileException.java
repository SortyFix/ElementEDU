package de.gaz.eedu.file.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MaliciousFileException extends ResponseStatusException
{
    private String path;

    public MaliciousFileException(String path, Throwable cause)
    {
        super(HttpStatus.BAD_GATEWAY, null, cause);
        this.path = path;
    }
}
