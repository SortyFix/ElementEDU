package de.gaz.eedu.user.exception;

import de.gaz.eedu.exception.OccupiedException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NameOccupiedException extends OccupiedException {
    private final String name;
}
