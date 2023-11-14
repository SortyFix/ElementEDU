package de.gaz.eedu.user.verfication.twofa.implementations;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import de.gaz.eedu.exception.HTTPRequestException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import org.apache.commons.codec.binary.Base32;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class TOTPImplementation implements TwoFactorMethodImplementation
{

    private static final Base32 base32 = new Base32();
    private static final int imageSize = 350;
    private static final int BYTE_SIZE = 20;

    @Override public @NotNull String setup(@NotNull UserService userService, @NotNull Long userID)
    {
        try
        {
            UserEntity userEntity = userService.loadEntityByIDSafe(userID);
            String secret = generateBase32();

            QRData qrData = new QRData(userEntity.getLoginName(), secret, "ElementEDU", "SHA1", 6, 30);
            return Base64.getEncoder().encodeToString(generateImage(qrData));
        }
        catch (WriterException | IOException exception)
        {
            throw new HTTPRequestException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override public boolean verify(@NotNull UserService userService, @NotNull Long userID, String code)
    {
        return true;
    }

    private byte @NotNull [] generateImage(@NotNull QRData qrData) throws WriterException, IOException
    {
        Writer writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(qrData.toURL(), BarcodeFormat.QR_CODE, imageSize, imageSize);

        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();

        ImageIO.write(image, "JPEG", jpegOutputStream);

        return jpegOutputStream.toByteArray();
    }

    public String generateBase32()
    {
        return new String(base32.encode(getRandomBytes()));
    }

    private byte @NotNull [] getRandomBytes()
    {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[BYTE_SIZE];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    private record QRData(@NotNull String type, @NotNull String label, @NotNull String secret, @NotNull String issuer,
                          @NotNull String algorithm, int digits, int period)
    {
        private QRData(@NotNull String label, @NotNull String secret, @NotNull String issuer,
                @NotNull String hashingAlgorithm, int digits, int period)
        {
            this("totp", label, secret, issuer, hashingAlgorithm, digits, period);
        }

        private @NotNull String toURL()
        {
            return String.format("otpauth://%s/%s?secret=%s&issuer=%s&algorithm=%s&digits=%s&period=%s",
                    uriEncode(type), uriEncode(label), uriEncode(secret), uriEncode(issuer), uriEncode(algorithm),
                    digits, period);
        }

        private @NotNull String uriEncode(@NotNull String text)
        {
            return URLEncoder.encode(text, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        }
    }
}
