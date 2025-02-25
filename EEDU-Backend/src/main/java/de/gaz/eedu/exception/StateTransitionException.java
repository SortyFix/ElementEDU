package de.gaz.eedu.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when an attempt is made to set a state that has already been set.
 * <p>
 * This indicates a conflict with the current state of the resource or entity. It is
 * typically used when an operation requires a specific state, and that state is
 * already in place, preventing the operation from proceeding. This often signals
 * a client error, as they are attempting an action that is not valid in the current context.
 * <p>
 * This exception results in an HTTP 422 Unprocessable Entity response.
 */
public class StateTransitionException extends ResponseStatusException
{
    public StateTransitionException(@NotNull String message)
    {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
