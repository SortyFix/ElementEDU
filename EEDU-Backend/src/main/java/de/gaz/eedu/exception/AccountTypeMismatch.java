package de.gaz.eedu.exception;

import de.gaz.eedu.user.AccountType;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AccountTypeMismatch extends ResponseStatusException
{
    private static final String MESSAGE_TEMPLATE = "The account type %s does not match the required account type %s";

    public AccountTypeMismatch(@NotNull AccountType expected, @NotNull AccountType required)
    {
        super(HttpStatus.BAD_REQUEST, String.format(MESSAGE_TEMPLATE, expected, required));
    }
}
