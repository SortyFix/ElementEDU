package de.gaz.eedu.entity;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

public class EntityExceptionHandler
{

    protected void catchException(@NotNull Runnable runnable, @NotNull RuntimeException runtimeException)
    {
        catchException(runnable, Throwable.class, runtimeException);
    }

    protected void catchException(@NotNull Runnable runnable, @NotNull Class<? extends Throwable> expected, @NotNull RuntimeException runtimeException)
    {
        catchException(runnable, expected, () -> runtimeException);
    }

    protected void catchException(@NotNull Runnable runnable, @NotNull Class<? extends Throwable> expected, @NotNull Supplier<RuntimeException> runtimeException)
    {
        try
        {
            runnable.run();
        }
        catch (Throwable exception)
        {
            if (expected.isAssignableFrom(exception.getClass()))
            {
                throw runtimeException.get();
            }
        }
    }

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
        if (!condition)
        {
            throw exception.get();
        }
    }

    protected @NotNull ResponseStatusException unauthorizedThrowable()
    {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}
