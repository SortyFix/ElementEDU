package de.gaz.eedu.user.exception;

import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.model.UserModel;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class LoginNameOccupiedException extends OccupiedException {
    private final UserModel user;
}
