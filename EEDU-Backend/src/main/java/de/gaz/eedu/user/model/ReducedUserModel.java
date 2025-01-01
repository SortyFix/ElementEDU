package de.gaz.eedu.user.model;

import jakarta.validation.constraints.NotNull;

public record ReducedUserModel(@NotNull Long id, @NotNull String firstName, @NotNull String lastName)
{
}
