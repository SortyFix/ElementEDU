package de.gaz.eedu.file.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DirectoryExistsException extends ResponseStatusException
{
    private String path;
    public DirectoryExistsException(String path)
    {
        this(HttpStatus.CONFLICT, path);
    }
    public DirectoryExistsException(HttpStatus status, String path){
        super(status);
    }
}
