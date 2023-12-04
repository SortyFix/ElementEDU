package de.gaz.eedu.user.verfication.twofa.implementations.totp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Base32;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Setter @Getter @AllArgsConstructor public class TOPTHandler
{

    private final HashingAlgorithm hashingAlgorithm;
    private final int digits;
    private int timePeriod, timeGap;


    public TOPTHandler(@NotNull HashingAlgorithm hashingAlgorithm)
    {
        this(hashingAlgorithm, 6, 30, 1);
    }

    public boolean isValidCode(long timeStamp, @NotNull String secret, @NotNull String code)
    {
        long currentBucket = Math.floorDiv(timeStamp, timePeriod);
        boolean success = false;
        for (int i = -timeGap; i <= timeGap; i++)
        {
            success = checkCode(secret, currentBucket + i, code) || success;
        }

        return success;
    }

    private boolean checkCode(@NotNull String secret, long counter, String code)
    {
        try
        {
            String actualCode = generateVerificationCode(secret, counter);
            return verifyCode(actualCode, code);
        }
        catch (InvalidKeyException | NoSuchAlgorithmException parent)
        {
            throw new IllegalStateException(String.format("Unable to verify code %s.", code), parent);
        }
    }

    private boolean verifyCode(@NotNull String var1, @NotNull String var2)
    {
        byte[] aBytes = var1.getBytes(), bBytes = var2.getBytes();

        if (aBytes.length != bBytes.length)
        {
            return false;
        }

        int result = 0;
        for (int i = 0; i < aBytes.length; i++)
        {
            result |= aBytes[i] ^ bBytes[i];
        }

        return result == 0;
    }

    public @NotNull String generateVerificationCode(@NotNull String key, long counter) throws InvalidKeyException, NoSuchAlgorithmException
    {
        return getDigitsFromHash(generateHash(key, counter));
    }

    private byte[] generateHash(@NotNull String key, long counter) throws InvalidKeyException, NoSuchAlgorithmException
    {
        byte[] data = new byte[8];
        long value = counter;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }

        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(key);
        SecretKeySpec signKey = new SecretKeySpec(decodedKey, getHashingAlgorithm().getHmacAlgorithm());
        Mac mac = Mac.getInstance(getHashingAlgorithm().getHmacAlgorithm());
        mac.init(signKey);

        return mac.doFinal(data);
    }

    private @NotNull String getDigitsFromHash(byte @NotNull [] hash) {
        int offset = hash[hash.length - 1] & 0xF;

        long truncatedHash = 0;

        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }

        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= (long) Math.pow(10, digits);

        return String.format("%0" + digits + "d", truncatedHash);
    }
}
