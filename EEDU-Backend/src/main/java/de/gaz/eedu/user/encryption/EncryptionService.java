package de.gaz.eedu.user.encryption;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service public class EncryptionService
{
    @Getter(AccessLevel.NONE) @Value("${jwt.secret}") private String secret;

    public @NotNull String generateKey(@NotNull String subject, @NotNull Map<String, ?> claims)
    {
        return Jwts.builder().subject(subject).claims(claims).issuedAt(new Date()).signWith(getKey()).compact();
    }

    public @NotNull Optional<UsernamePasswordAuthenticationToken> validate(@NotNull String token)
    {
        String user = Jwts.parser().verifyWith(getKey()).build().parseUnsecuredClaims(token.substring("Baerer".length())).getPayload().getSubject();
        if (user != null)
        {
            return Optional.of(new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>()));
        }
        return Optional.empty();
    }

    private @NotNull SecretKey getKey()
    {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
