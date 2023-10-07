package de.gaz.sp.user.exception;

import de.gaz.sp.exception.OccupiedException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NameOccupiedException extends OccupiedException {
    private final String name;
}
