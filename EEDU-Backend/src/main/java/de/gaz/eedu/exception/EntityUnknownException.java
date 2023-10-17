package de.gaz.eedu.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EntityUnknownException extends IllegalStateException
{
    private final long id;
}
