package de.gaz.eedu.user.verification.credentials.implementations;

import de.gaz.eedu.user.verification.credentials.CredentialEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequiredArgsConstructor @Getter(AccessLevel.PROTECTED)
public class PasswordCredential implements Credential
{
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public @NotNull String creation(@NotNull CredentialEntity credentialEntity)
    {
        credentialEntity.setEnabled(true); // no enabling required.
        credentialEntity.setSecret(getPasswordEncoder().encode(credentialEntity.getData()));
        return getPasswordEncoder().encode(credentialEntity.getData());
    }

    @Override
    public boolean verify(@NotNull CredentialEntity credentialEntity, @NotNull String code)
    {
        return getPasswordEncoder().matches(code, credentialEntity.getSecret());
    }

    @Override
    public boolean enable(@NotNull CredentialEntity credentialEntity, @NotNull String code)
    {
        return true;
    }
}
