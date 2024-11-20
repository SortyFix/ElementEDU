package de.gaz.eedu.user.verification.credentials.implementations;

import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.verification.credentials.CredentialEntity;
import de.gaz.eedu.user.verification.credentials.implementations.totp.HashingAlgorithm;
import de.gaz.eedu.user.verification.credentials.implementations.totp.TOPTHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.codec.binary.Base32;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.time.Instant;

@AllArgsConstructor @Getter(AccessLevel.PROTECTED) public class TOTPCredential implements Credential
{
    private static final int BYTE_SIZE = 20;
    private static final Base32 BASE_32 = new Base32();
    @NotNull private final TOPTHandler toptHandler;

    @Override public void creation(@NotNull CredentialEntity credentialEntity)
    {
        credentialEntity.setSecret(generateBase32());
    }

    @Override public @Nullable TOTPCredential.TOTPData getSetupData(@NotNull CredentialEntity credentialEntity)
    {
        UserEntity userEntity = credentialEntity.getUser();
        String secret = credentialEntity.getSecret();
        return new TOTPData(userEntity.getLoginName(), secret, HashingAlgorithm.SHA1.getFriendlyName(), 6, 30);
    }

    @Override public boolean verify(@NotNull CredentialEntity credentialEntity, @NotNull String code)
    {
        return validate(credentialEntity, code);
    }

    @Override public boolean enable(@NotNull CredentialEntity credentialEntity, @NotNull String code)
    {
        return validate(credentialEntity, code);
    }

    @Contract(pure = true, value = "-> new") public @NotNull String generateBase32()
    {
        return new String(BASE_32.encode(getRandomBytes()));
    }

    @Contract(pure = true, value = "-> new") private byte @NotNull [] getRandomBytes()
    {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[BYTE_SIZE];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    private boolean validate(@NotNull CredentialEntity credentialEntity, @NotNull String code)
    {
        return getToptHandler().isValidCode(Instant.now().getEpochSecond(), credentialEntity.getSecret(), code);
    }

    public record TOTPData(@NotNull String loginName, @NotNull String secret, @NotNull String algorithm, int digits,
            int period)
    {}

}
