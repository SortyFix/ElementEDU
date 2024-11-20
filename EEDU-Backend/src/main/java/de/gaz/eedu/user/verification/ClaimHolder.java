package de.gaz.eedu.user.verification;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A record class that helps with creating claims.
 * <p>
 * This record is a helper class for creating Claims. These claims are saved in a
 * {@link Map} wit the types String and Object.
 * With this class multiple objects can be added to such a list as key and value are both included within this
 * object.
 *
 * @param key     the key of the claim.
 * @param content the content of the claim.
 * @param <T>     the type of the content.
 */
public record ClaimHolder<T>(@NotNull String key, @NotNull T content) {}
