package de.gaz.eedu.user.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GroupConflictException extends ResponseStatusException
{
    public GroupConflictException(@NotNull String reason)
    {
        super(HttpStatus.CONFLICT, reason);
    }
}
