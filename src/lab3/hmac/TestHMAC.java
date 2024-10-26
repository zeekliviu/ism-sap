package lab3.hmac;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class TestHMAC {
    static void printHex(byte[] bytes) {
        for (byte b : bytes) {
            System.out.printf("%02x", b);
        }
        System.out.println();
    }

    static byte[] getHMAC(String fileName, String algorithm, String password) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        Mac hmac = Mac.getInstance(algorithm);
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), algorithm);
        hmac.init(key);

        // read the file and process it
        File file = new File(fileName);
        if(!file.exists())
        {
            throw new UnsupportedOperationException("The file is not there.");
        }
        try(var br = new BufferedInputStream(new FileInputStream(file)))
        {
            byte[] buffer = new byte[8];
            int bytesRead;
            while((bytesRead = br.read(buffer)) != -1)
            {
                hmac.update(buffer, 0, bytesRead);
            }
        }
        return hmac.doFinal();
    }

    static byte[] getPBKDF(String userPass, String algorithm, String salt, int iterations, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(userPass.toCharArray(), salt.getBytes(), iterations, keyLength);
        SecretKeyFactory pbkdf = SecretKeyFactory.getInstance(algorithm);
        SecretKey key = pbkdf.generateSecret(pbeKeySpec);
        return key.getEncoded();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeyException, InvalidKeySpecException {
        byte[] hmac = getHMAC("Message.txt", "HmacSHA1", "ism1234");
        printHex(hmac);

        byte[] pbkdf = getPBKDF("ism1234", "PBKDF2WithHmacSHA1", "rd@h1", 1000, 128);
        printHex(pbkdf);

        //benchmark PBKDF performance
        long tStart = System.currentTimeMillis();
        pbkdf = getPBKDF("ism1234", "PBKDF2WithHmacSHA1", "rd@h1", 100_000_000, 128);
        long tEnd = System.currentTimeMillis();
        System.out.println("PBKDF took " + (tEnd - tStart) + " ms");
    }
}
