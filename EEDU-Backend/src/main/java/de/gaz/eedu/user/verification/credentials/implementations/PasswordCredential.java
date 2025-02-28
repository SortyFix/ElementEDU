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

@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class PasswordCredential implements Credential
{
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void creation(@NotNull CredentialEntity credentialEntity)
    {
        String password = credentialEntity.getData();
        if (!password.matches(
                "^(?=(.*[a-z])+)(?=(.*[A-Z])+)(?=(.*[0-9])+)(?=(.*[!\"#$%&'()*+,\\-./:;<=>?@\\[\\\\\\]^_`{|}~])+).{6,}$"))
        {
            throw new InsecurePasswordException();
        }

        prohibitDuplication(credentialEntity);

        credentialEntity.setEnabled(true); // no enabling required.
        credentialEntity.setData(getPasswordEncoder().encode(password));
    }

    @Override
    public boolean verify(@NotNull CredentialEntity credentialEntity, @NotNull String password)
    {
        return getPasswordEncoder().matches(password, credentialEntity.getData());
    }

    /**
     * This method prohibits duplicated passwords
     * <p>
     * This method iterates over the passwords from the specified user and validates each of them.
     * If one of the password returns {@code true} when running {@link #verify(CredentialEntity, String)} a
     * {@link ResponseStatusException} will get thrown.
     *
     * @param credential the credential which contains the data of the newly registered password.
     * @throws ResponseStatusException when a similar password has been found within the users passwords.
     */
    private void prohibitDuplication(@NotNull CredentialEntity credential) throws ResponseStatusException
    {
        Set<CredentialEntity> passwords = credential.getUser().getCredentials(CredentialMethod.PASSWORD);
        if (passwords.stream().anyMatch(current -> verify(current, credential.getData()) && current.isEnabled()))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
