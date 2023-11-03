package de.gaz.eedu.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor @Getter public class CreationException extends IllegalStateException {

    private final HttpStatus status;

}
