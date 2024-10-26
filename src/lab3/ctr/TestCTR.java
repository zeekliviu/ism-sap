package lab3.ctr;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class TestCTR {
    static void DESEncrypt(String inputFile, String outputFile, byte[] key) throws Exception {
        File input = new File(inputFile);
        if(!input.exists())
        {
            throw new UnsupportedOperationException("The file is not there.");
        }
        File output = new File(outputFile);
        output.createNewFile();

        Cipher cipher = Cipher.getInstance("DES/CTR/NoPadding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "DES");
        byte[] iv = new byte[cipher.getBlockSize()];
        iv[2] = (byte)0xff;
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        try(var fis = new FileInputStream(input))
        {
            byte[] buffer = new byte[cipher.getBlockSize()];
            int bytesRead;
            //IV values:
            // 1. hard coded known value
            // 2. known value or any value stored in the ciphertext (at the beginning)

            // option 2
            // IV has the 3rd byte with all bits 1

            try(var fos = new FileOutputStream(output))
            {
                fos.write(iv);
                while((bytesRead = fis.read(buffer)) != -1)
                {
                    fos.write(cipher.update(buffer, 0, bytesRead));
                }
                fos.write(cipher.doFinal());
            }
        }
    }

    static void DESDecryt(String inputFile, String outputFile, byte[] key) throws Exception {
        File input = new File(inputFile);
        if(!input.exists())
        {
            throw new UnsupportedOperationException("The file is not there.");
        }
        File output = new File(outputFile);
        output.createNewFile();

        Cipher cipher = Cipher.getInstance("DES/CTR/NoPadding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "DES");
        byte[] iv = new byte[cipher.getBlockSize()];
        try(var fis = new FileInputStream(input))
        {
            int result = fis.read(iv);
            if(result != iv.length)
            {
                throw new UnsupportedOperationException("IV is not there.");
            }
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] buffer = new byte[cipher.getBlockSize()];
            int bytesRead;
            try(var fos = new FileOutputStream(output))
            {
                while((bytesRead = fis.read(buffer)) != -1)
                {
                    fos.write(cipher.update(buffer, 0, bytesRead));
                }
                fos.write(cipher.doFinal());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        DESEncrypt("Message.txt", "MessageCTR.enc", "ism12345".getBytes());
        System.out.println("Encryption done.");
        DESDecryt("MessageCTR.enc", "MessageCTRDec.txt", "ism12345".getBytes());
        System.out.println("Decryption done.");
    }
}
