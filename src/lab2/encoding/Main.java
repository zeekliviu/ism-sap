package lab2.encoding;

import java.util.Base64;

public class Main {
    static void printHex(byte[] bytes) {
        for (byte b : bytes) {
            System.out.printf("%02X", b);
        }
        System.out.println();
    }
    public static void main(String[] args) {
        // managing binary data as strings

        byte[] values = {(byte)0x00, (byte)0x01, (byte)0x30, (byte)0x62};
        String str = new String(values);
        System.out.println("The string is: " + str);

        // encoding base64
        String value1Base64 = Base64.getEncoder().encodeToString(values);
        System.out.println("The base64 encoding of the values is: " + value1Base64);

        // decoding base64
        byte[] decodedValue = Base64.getDecoder().decode(value1Base64);
        System.out.println("The decoded value is: ");
        printHex(decodedValue);
    }
}
