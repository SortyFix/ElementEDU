package de.gaz.eedu.user.verfication.twofa.implementations.totp;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public record QRData(@NotNull String type, @NotNull String label, @NotNull String secret, @NotNull String issuer,
                     @NotNull HashingAlgorithm algorithm, int digits, int period)
{
    public QRData(@NotNull String label, @NotNull String secret, @NotNull String issuer,
            @NotNull HashingAlgorithm hashingAlgorithm, int digits, int period)
    {
        this("totp", label, secret, issuer, hashingAlgorithm, digits, period);
    }

    public @NotNull String toURI()
    {
        return String.format("otpauth://%s/%s?secret=%s&issuer=%s&algorithm=%s&digits=%s&period=%s", uriEncode(type),
                uriEncode(label), uriEncode(secret), uriEncode(issuer), uriEncode(algorithm.getFriendlyName()), digits, period);
    }

    public byte @NotNull [] generateImage(int imageSize) throws WriterException, IOException
    {
        Writer writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(toURI(), BarcodeFormat.QR_CODE, imageSize, imageSize);

        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();

        ImageIO.write(image, "JPEG", jpegOutputStream);

        return jpegOutputStream.toByteArray();
    }

    private @NotNull String uriEncode(@NotNull String text)
    {
        return URLEncoder.encode(text, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }
}
