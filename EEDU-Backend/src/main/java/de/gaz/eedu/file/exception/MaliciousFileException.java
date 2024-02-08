package de.gaz.eedu.file.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MaliciousFileException extends ResponseStatusException
{
    private String path;

    public MaliciousFileException(String path)
    {
        this(HttpStatus.NOT_FOUND, path);
    }

    public MaliciousFileException(HttpStatus status, String path) {super(status);}
}
