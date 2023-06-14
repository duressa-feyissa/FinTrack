package Common;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class Common {

    public static String path  = "secretkey.key";
    public static String CreateId() {
        return UUID.randomUUID().toString();
    }

    public static SecretKey readKeyFromFile() throws NoSuchAlgorithmException, IOException {
        byte[] keyBytes;

        try (FileInputStream fis = new FileInputStream(getFilePath())) {
            keyBytes = fis.readAllBytes();
        }

        return new SecretKeySpec(keyBytes, "AES");
    }

    public static String encryptMessage(String message, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decryptMessage(String encryptedMessage, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }
    private static String getFilePath() {
        return Common.path;
    }
}
