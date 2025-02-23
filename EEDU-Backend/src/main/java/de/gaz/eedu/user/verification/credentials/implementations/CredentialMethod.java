package de.gaz.eedu.user.verification.credentials.implementations;

import de.gaz.eedu.user.verification.credentials.implementations.totp.HashingAlgorithm;
import de.gaz.eedu.user.verification.credentials.implementations.totp.TOPTHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequiredArgsConstructor
@Getter
public enum CredentialMethod
{
    PASSWORD(new PasswordCredential(new BCryptPasswordEncoder()), false),
    EMAIL(new EmailCredential(), true),
    SMS(new SMSCredential(), true),
    TOTP(new TOTPCredential(new TOPTHandler(HashingAlgorithm.SHA1)), true);

    private final Credential credential;
    private final boolean enablingRequired;

    @Contract(pure = true) public static int bitMask(@NotNull CredentialMethod @NotNull ... methods)
    {
        int value = 0;
        for (CredentialMethod method : methods)
        {
            value |= method.getBitValue();
        }
        return value;
    }

    public boolean contains(int bitmask)
    {
        return (bitmask & getBitValue()) != 0;
    }

    public int getBitValue()
    {
        return 1 << ordinal();
    }
}
