package de.gaz.eedu.user.encryption;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service public class EncryptionService
{
    @Value("${jwt.secret}") private String secret;

    public @NotNull String generateKey(@NotNull String subject)
    {
        return Jwts.builder().subject(subject).issuedAt(new Date()).signWith(getKey()).compact();
    }

    public @NotNull Optional<UsernamePasswordAuthenticationToken> validate(@NotNull String token, @NotNull AuthorityFactory authorityFactory)
    {
        long userID = Long.parseLong(Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload().getSubject());
        return Optional.of(new UsernamePasswordAuthenticationToken(userID, null, authorityFactory.get(userID)));
    }

    private @NotNull SecretKey getKey()
    {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public @NotNull BCryptPasswordEncoder getEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
