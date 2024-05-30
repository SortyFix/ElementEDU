package de.gaz.eedu.user.verification.credentials.implementations;

import com.google.zxing.WriterException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.verification.credentials.CredentialEntity;
import de.gaz.eedu.user.verification.credentials.implementations.totp.HashingAlgorithm;
import de.gaz.eedu.user.verification.credentials.implementations.totp.QRData;
import de.gaz.eedu.user.verification.credentials.implementations.totp.TOPTHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.codec.binary.Base32;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@AllArgsConstructor @Getter(AccessLevel.PROTECTED) public class TOTPCredential implements Credential
{
    private static final int BYTE_SIZE = 20;
    private static final Base32 BASE_32 = new Base32();
    @NotNull private final TOPTHandler toptHandler;

    @Override public @NotNull String creation(@NotNull CredentialEntity credentialEntity)
    {
        try
        {
            UserEntity userEntity = credentialEntity.getUser();
            String secret = generateBase32();
            credentialEntity.setSecret(secret);

            QRData qrData = new QRData(userEntity.getLoginName(), secret, "ElementEDU", HashingAlgorithm.SHA1, 6, 30);
            return Base64.getEncoder().encodeToString(qrData.generateImage(350));
        }
        catch (WriterException | IOException exception)
        {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
}
