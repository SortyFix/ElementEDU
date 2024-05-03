package de.gaz.eedu.entity;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

public class EntityExceptionHandler
{

    protected void validate(boolean condition)
    {
        validate(condition, new IllegalStateException());
    }

    protected void validate(boolean condition, @NotNull RuntimeException runtimeException)
    {
        validate(condition, () -> runtimeException);
    }

    protected void validate(boolean condition, @NotNull Supplier<? extends RuntimeException> exception)
    {
        if(!condition)
        {
            throw exception.get();
        }
    }

    protected @NotNull ResponseStatusException unauthorizedThrowable()
    {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}
