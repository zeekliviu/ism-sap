package lab3.ecb;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class TestECB {
    static void encrypt(String inputFile, String outputFile, byte[] key, String algorithm) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        File file = new File(inputFile);
        if(!file.exists())
        {
            throw new UnsupportedOperationException("The file is not there.");
        }
        File outFile = new File(outputFile);
        outFile.createNewFile();

        Cipher cipher = Cipher.getInstance(String.format("%s/ECB/PKCS5Padding", algorithm));
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        try(var fis = new FileInputStream(file))
        {
            byte[] buffer = new byte[cipher.getBlockSize()];
            int bytesRead;
            try(var fos = new FileOutputStream(outFile))
            {
                while((bytesRead = fis.read(buffer)) != -1)
                {
                    fos.write(cipher.update(buffer, 0, bytesRead));
                }
                fos.write(cipher.doFinal());
            }
        }
    }

    static void decrypt(String inputFile, String outputFile, byte[] key, String algorithm) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        File file = new File(inputFile);
        if(!file.exists())
        {
            throw new UnsupportedOperationException("The file is not there.");
        }
        File outFile = new File(outputFile);
        outFile.createNewFile();

        Cipher cipher = Cipher.getInstance(String.format("%s/ECB/PKCS5Padding", algorithm));
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        try(var fis = new FileInputStream(file))
        {
            byte[] buffer = new byte[cipher.getBlockSize()];
            int bytesRead;
            try(var fos = new FileOutputStream(outFile))
            {
                while((bytesRead = fis.read(buffer)) != -1)
                {
                    fos.write(cipher.update(buffer, 0, bytesRead));
                }
                fos.write(cipher.doFinal());
            }
        }
    }

    public static void main(String[] args) throws NoSuchPaddingException, IOException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        encrypt("Message.txt", "Message.enc", "ism12345password".getBytes(), "AES");
        System.out.println("Encryption done.");
        decrypt("Message.enc", "Message.dec", "ism12345password".getBytes(), "AES");
        System.out.println("Decryption done.");
    }
}
