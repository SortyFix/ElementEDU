package de.gaz.sp.user.exception;

import de.gaz.sp.exception.OccupiedException;
import de.gaz.sp.user.UserModel;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class UserEmailOccupiedException extends OccupiedException {
    private final UserModel user;
}
