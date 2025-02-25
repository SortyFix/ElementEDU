package de.gaz.eedu.user.group;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GroupUnprocessableException extends ResponseStatusException
{
    public GroupUnprocessableException(@NotNull String entity)
    {
        super(HttpStatus.UNPROCESSABLE_ENTITY, String.format("The group %s cannot be altered, attached or detached.", entity));
    }
}
