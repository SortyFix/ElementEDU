package de.gaz.eedu.user.verification;

import de.gaz.eedu.user.verification.authority.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record TokenData(@Nullable Claims parent, long userId, boolean advanced, Set<String> restrictedClaims,
                        @NotNull Map<String, Object> additionalClaims)
{

    public TokenData(@Nullable Claims parent, long userId, boolean advanced, @NotNull Set<String> restrictedClaims, @NotNull Map<String, Object> additionalClaims)
    {
        this.parent = parent;
        this.userId = userId;
        this.advanced = advanced;
        this.restrictedClaims = new HashSet<>(restrictedClaims);
        this.additionalClaims = new HashMap<>(additionalClaims);

        validate(additionalClaims().keySet(), restrictedClaims());
    }

    public TokenData(long userId, boolean advanced, @NotNull Set<String> restrictedClaims, @NotNull ClaimHolder<?>... claimHolder)
    {
        this(
                null,
                userId,
                advanced,
                restrictedClaims,
                Stream.of(claimHolder).collect(Collectors.toMap(ClaimHolder::key, ClaimHolder::content)));
    }

    private static void validate(@NotNull Set<String> keys, Set<String> elements) throws InvalidTokenException
    {
        if (!keys.containsAll(elements))
        {
            throw new InvalidTokenException();
        }
    }

    @Contract("_, _ -> new")
    public static @NotNull TokenData deserialize(@NotNull Claims claims, @NotNull String... restrictedClaims) throws InvalidTokenException
    {
        Map<String, Object> additionalClaims = new HashMap<>(claims);
        Set<String> restricted = Set.of(restrictedClaims);
        additionalClaims.remove("userId");
        additionalClaims.remove("advanced");

        // get rid of this, will be generated with next thing
        additionalClaims.remove("sub");
        additionalClaims.remove("iat");
        additionalClaims.remove("exo");

        validate(additionalClaims.keySet(), restricted);

        long userId = claims.get("userId", Long.class);
        boolean advanced = claims.get("advanced", Boolean.class);
        return new TokenData(claims, userId, advanced, restricted, additionalClaims);
    }

    @Contract("_, _ -> new")
    public static @NotNull TokenData deserialize(@NotNull String key, @NotNull String token) throws InvalidTokenException
    {
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        return TokenData.deserialize(claims);
    }

    public @Unmodifiable @NotNull Map<String, Object> toMap()
    {
        Map<String, Object> claims = new HashMap<>(additionalClaims());
        claims.put("userId", userId());
        claims.put("advanced", advanced());
        return Collections.unmodifiableMap(claims);
    }

    public boolean restrictClaim(@NotNull String claim)
    {
        return this.restrictedClaims.add(claim);
    }

    public boolean addClaim(@NotNull String key, @NotNull Object value)
    {
        return addClaim(false, key, value);
    }

    public boolean addClaim(boolean override, @NotNull String key, @NotNull Object value)
    {
        if (additionalClaims().containsKey(key) && !override)
        {
            return false;
        }

        additionalClaims.put(key, value);
        return true;
    }

    public boolean removeClaim(@NotNull String key)
    {
        if (restrictedClaims().contains(key))
        {
            return false;
        }

        return Objects.nonNull(additionalClaims.remove(key));
    }

    public <T> @NotNull T get(@NotNull String key, @NotNull Class<T> type) throws ClassCastException
    {
        return type.cast(additionalClaims().get(key));
    }

    @Override public @Unmodifiable @NotNull Map<String, Object> additionalClaims()
    {
        return Collections.unmodifiableMap(additionalClaims);
    }

    @Override public @Unmodifiable @NotNull Set<String> restrictedClaims()
    {
        return restrictedClaims;
    }

    public @NotNull Optional<Claims> getParent()
    {
        return Optional.ofNullable(parent());
    }
}
