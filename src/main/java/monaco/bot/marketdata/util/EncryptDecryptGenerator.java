package monaco.bot.marketdata.util;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptDecryptGenerator {

    @Value("${secret-key}")
    private String secretKey;

    @Value("${algorithm}")
    private String algorithm;

    public String encryptData(String data) throws Exception {
        SecretKey key = new SecretKeySpec(secretKey.getBytes(), algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @SneakyThrows
    public String decryptData(String encryptedData) {
        SecretKey key = new SecretKeySpec(secretKey.getBytes(), algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

}