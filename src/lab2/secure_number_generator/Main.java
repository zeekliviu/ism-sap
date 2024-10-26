package lab2.secure_number_generator;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Main {
    static void printHex(byte[] bytes) {
        for (byte b : bytes) {
            System.out.printf("%02x", b);
        }
        System.out.println();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        // use crypto safe PRNG
        // don't use Random

        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] desKey = new byte[8];

        // random, you don't get the same key every time
        secureRandom.setSeed(new byte[] {(byte)0xff, (byte)0xa8});
        secureRandom.nextBytes(desKey);
        System.out.println("The generated DES key is: ");
        printHex(desKey);

        byte[] desKey2 = new byte[8];
        secureRandom.nextBytes(desKey2);
        System.out.println("The generated DES key is: ");
        printHex(desKey2);
    }
}
