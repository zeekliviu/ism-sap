package lab3.otp;

import java.security.SecureRandom;
import java.util.Base64;

public class TestOTP {

    static void printHex(byte[] bytes) {
        for (byte b : bytes) {
            System.out.printf("%02x", b);
        }
        System.out.println();
    }

    static byte[] generateRandomKey(int keySizeInBytes) throws Exception
    {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] random = new byte[keySizeInBytes];
        secureRandom.nextBytes(random);
        return random;
    }

    static byte[] otpEncryptDecrypt(byte[] plainText, byte[] key)
    {
        if(plainText.length != key.length)
        {
            throw new UnsupportedOperationException("The key and the message should be of the same length.");
        }
        byte[] cipherText = new byte[plainText.length];
        for(int i = 0; i < plainText.length; i++)
        {
            cipherText[i] = (byte)(plainText[i] ^ key[i]);
        }
        return cipherText;
    }

    public static void main(String[] args) throws Exception {
        String message = "The requirements for tommorow are...";
        byte[] randomKey = generateRandomKey(message.length());
        byte[] cipherText = otpEncryptDecrypt(message.getBytes(), randomKey);
        String randomKeyString = Base64.getEncoder().encodeToString(randomKey);
        System.out.print("Cipher text: ");
        printHex(cipherText);
        System.out.printf("Random key: %s%n", randomKeyString);

        // decryption
        byte[] initialMessage = otpEncryptDecrypt(cipherText, randomKey);
        System.out.printf("Initial message: %s%n", new String(initialMessage));

    }
}
