package de.gaz.eedu.course.classroom.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TutorConflictException extends ResponseStatusException
{
    public TutorConflictException()
    {
        super(HttpStatus.CONFLICT, "There is already a tutor for that class.");
    }
}
