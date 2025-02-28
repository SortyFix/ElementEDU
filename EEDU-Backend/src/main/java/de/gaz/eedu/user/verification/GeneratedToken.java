package de.gaz.eedu.user.verification;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

public record GeneratedToken(@NotNull JwtTokenType type, @NotNull String jwt, @NotNull TokenData tokenData)
{
    public static @NotNull GeneratedToken toToken(@NotNull String key, @NotNull JwtTokenType type, @NotNull Instant expires, @NotNull TokenData tokenData)
    {
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        String jwt = Jwts.builder().subject(type.name()).claims(tokenData.toMap()).expiration(Date.from(expires)).issuedAt(
                new Date()).signWith(secretKey).compact();
        return new GeneratedToken(type, jwt, TokenData.deserialize(key, jwt));
    }
}
