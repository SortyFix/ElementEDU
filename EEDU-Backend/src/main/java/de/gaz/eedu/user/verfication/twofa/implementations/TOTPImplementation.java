package de.gaz.eedu.user.verfication.twofa.implementations;

import com.google.zxing.WriterException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.verfication.twofa.TwoFactorEntity;
import de.gaz.eedu.user.verfication.twofa.implementations.totp.HashingAlgorithm;
import de.gaz.eedu.user.verfication.twofa.implementations.totp.QRData;
import de.gaz.eedu.user.verfication.twofa.implementations.totp.TOPTHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;

@AllArgsConstructor @Getter(AccessLevel.PROTECTED) public class TOTPImplementation implements TwoFactorMethodImplementation
{
    @NotNull private final TOPTHandler toptHandler;

    @Override public @NotNull String creation(@NotNull TwoFactorEntity twoFactorEntity)
    {
        try
        {
            UserEntity userEntity = twoFactorEntity.getUser();
            String secret = twoFactorEntity.getSecret();

            QRData qrData = new QRData(userEntity.getLoginName(), secret, "ElementEDU", HashingAlgorithm.SHA1, 6, 30);
            return Base64.getEncoder().encodeToString(qrData.generateImage(350));
        }
        catch (WriterException | IOException exception)
        {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override public boolean verify(@NotNull TwoFactorEntity twoFactorEntity, @NotNull String code)
    {
        return validate(twoFactorEntity, code);
    }

    @Override public boolean enable(@NotNull TwoFactorEntity twoFactorEntity, @NotNull String code)
    {
        return validate(twoFactorEntity, code);
    }

    private boolean validate(@NotNull TwoFactorEntity twoFactorEntity, @NotNull String code)
    {
        return getToptHandler().isValidCode(Instant.now().getEpochSecond(), twoFactorEntity.getSecret(), code);
    }
}
