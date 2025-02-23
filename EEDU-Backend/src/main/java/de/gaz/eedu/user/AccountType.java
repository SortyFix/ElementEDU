package de.gaz.eedu.user;

import jakarta.validation.constraints.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum AccountType {

    ADMINISTRATOR,
    TEACHER,
    STUDENT;

    @Override public @org.jetbrains.annotations.NotNull String toString()
    {
        return name().toLowerCase();
    }

    public static @NotNull @Unmodifiable Set<String> groupSet() {
        return Stream.of(values()).map(AccountType::toString).collect(Collectors.toUnmodifiableSet());
    }
}
