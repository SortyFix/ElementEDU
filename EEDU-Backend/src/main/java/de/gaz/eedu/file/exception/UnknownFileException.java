package de.gaz.eedu.file.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnknownFileException extends ResponseStatusException
{
    public UnknownFileException(long id)
    {
        this(HttpStatus.NOT_FOUND, id);
    }

    public UnknownFileException(HttpStatus status, long id){
        super(status);
    }
}
