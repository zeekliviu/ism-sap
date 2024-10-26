package lab2.message_digest;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.security.*;

public class Main {

    static void printHex(byte[] bytes) {
        for (byte b : bytes) {
            System.out.printf("%02X", b);
        }
        System.out.println();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        // checking and using different providers - BouncyCastle
        String BouncyCastleProvider = "BC";

        // check if the provider is available
        Provider provider = Security.getProvider(BouncyCastleProvider);
        if(provider == null)
        {
            System.out.println("Bouncy Castle provider is NOT available.");
        }
        else
        {
            System.out.println("Bouncy Castle provider is available.");
        }

        // load BC provider
        Security.addProvider(new BouncyCastleProvider());

        // check if the SUN provider is available
        provider = Security.getProvider("SUN");
        if(provider == null)
        {
            System.out.println("SUN provider is NOT available.");
        }
        else
        {
            System.out.println("SUN provider is available.");
        }

        String message = "ISM";

        //hashing a string
        MessageDigest md = MessageDigest.getInstance("SHA-1", BouncyCastleProvider);

        //compute the hash in one step - the input is small enough
        byte[] hashValue = md.digest(message.getBytes());

        System.out.println("The hash value of the message is: ");
        printHex(hashValue);

        //compute the hash of a file
        //we real all file types as binary
        File file = new File("Message.txt");
        if(!file.exists())
        {
            System.out.println("The file is not there.");
        }
        try(var br = new BufferedInputStream(new FileInputStream("Message.txt"))) {
            md = MessageDigest.getInstance("MD5", BouncyCastleProvider);
            byte[] buffer = new byte[8];
            int noBytes;
            while ((noBytes = br.read(buffer)) != -1) {
                md.update(buffer, 0, noBytes);
            }
        }
        System.out.println("The hash of the file is");
        printHex(md.digest());
    }
}
