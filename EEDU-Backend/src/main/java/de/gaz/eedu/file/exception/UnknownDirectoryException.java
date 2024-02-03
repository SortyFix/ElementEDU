package de.gaz.eedu.file.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnknownDirectoryException extends ResponseStatusException
{
    private String path;
    public UnknownDirectoryException(String path)
    {
        this(HttpStatus.NOT_FOUND, path);
    }
    public UnknownDirectoryException(HttpStatus status, String path){
        super(status);
    }
}

