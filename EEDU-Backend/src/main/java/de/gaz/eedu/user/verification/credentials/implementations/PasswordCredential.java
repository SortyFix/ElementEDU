package de.gaz.eedu.user.verification.credentials.implementations;

import de.gaz.eedu.user.exception.InsecurePasswordException;
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
    public void creation(@NotNull CredentialEntity credentialEntity)
    {
        String password = credentialEntity.getData();
        if (!password.matches("^(?=(.*[a-z])+)(?=(.*[A-Z])+)(?=(.*[0-9])+)(?=(.*[!\"#$%&'()*+,\\-./:;<=>?@\\[\\\\\\]^_`{|}~])+).{6,}$"))
        {
            throw new InsecurePasswordException();
        }

        credentialEntity.setEnabled(true); // no enabling required.
        credentialEntity.setData(getPasswordEncoder().encode(password));
    }

    @Override
    public boolean verify(@NotNull CredentialEntity credentialEntity, @NotNull String password)
    {
        System.out.println("test password " + password + " with " + credentialEntity.getData());
        return getPasswordEncoder().matches(password, credentialEntity.getData());
    }
}
