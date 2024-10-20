package de.gaz.eedu.user.verification.credentials.implementations;

import de.gaz.eedu.user.exception.InsecurePasswordException;
import de.gaz.eedu.user.verification.credentials.CredentialEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RequiredArgsConstructor @Getter(AccessLevel.PROTECTED)
public class PasswordCredential implements Credential
{
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void creation(@NotNull CredentialEntity credentialEntity)
    {
        String password = credentialEntity.getData();
        if (!password.matches("^(?=(.*[a-z])+)(?=(.*[A-Z])+)(?=(.*[0-9])+)(?=(.*[!\"#$%&'()*+,\\-./:;<=>?@\\[\\\\\\]^_`{|}~])+).{6,}$"))
        {
            throw new InsecurePasswordException();
        }

        Set<CredentialEntity> passwords = credentialEntity.getUser().getCredentials(CredentialMethod.PASSWORD);
        if(passwords.stream().anyMatch(credential -> verify(credential, password) && credential.isEnabled()))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        credentialEntity.setEnabled(true); // no enabling required.
        credentialEntity.setData(getPasswordEncoder().encode(password));
    }

    @Override
    public boolean verify(@NotNull CredentialEntity credentialEntity, @NotNull String password)
    {
        return getPasswordEncoder().matches(password, credentialEntity.getData());
    }
}
