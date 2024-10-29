package ro.ase.ism.sap.zecheru.liviu_ioan;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

//rename the class with your name
//use a package with the next pattern
//	ro.ase.ism.sap.lastname.firstname
public class ZecheruLiviuIoan_Upload {

    static String getHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    // 1. Step 1: return your file name
    public static String findFile(String hash) throws NoSuchAlgorithmException, IOException {
        File dir = new File("src/safecorp_random_messages");
        if(!dir.exists())
        {
            System.out.println("File does not exist.");
            return null;
        }
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        var files = dir.listFiles();
        for(var f : files)
        {
            try(var fis = new FileInputStream(f))
            {
                byte[] content = fis.readAllBytes();
                byte[] result = sha256.digest(content);
                if(getHex(result).equals(hash))
                {
                    System.out.printf("The required file name is: %s", f.getName());
                    return f.getName();
                }
            }
        }
        return null;
    };

    // 2. Step 2: Generate HMAC for Authentication
    public static void generateHMAC(String filename, String sharedSecret) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try(var fis = new FileInputStream("src/safecorp_random_messages/"+filename))
        {
            var content = fis.readAllBytes();
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec spec = new SecretKeySpec(sharedSecret.getBytes(), "HmacSHA256");
            hmac.init(spec);

            try(var bos = new BufferedWriter(new FileWriter("hmac.txt")))
            {
                bos.write(getHex(hmac.doFinal(content)));
            }
        }
    }

    // 3. Step 3: Derive Key with PBKDF2
    public static byte[] deriveKeyWithPBKDF2(
            String password, int noIterations, int keySize) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String salt = "ism2024";
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), noIterations, keySize);
        SecretKeyFactory pbkdf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        SecretKey key = pbkdf.generateSecret(pbeKeySpec);
        try(var fw = new FileWriter("salt.txt"))
        {
            fw.write(salt);
        }
        return key.getEncoded();
    }

    // 4. Step 4: Encrypt File with AES and Save IV
    public static void encryptFileWithAES(String filename, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        byte[] iv = new byte[cipher.getBlockSize()];
        iv[12] = (byte) 0b10000000;
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        try (var fis = new FileInputStream("src/safecorp_random_messages/" + filename))
        {
            byte[] content = fis.readAllBytes();
            byte[] result = cipher.doFinal(content);
            var base64 = Base64.getEncoder();
            try(var fw = new FileWriter("encrypted.txt"))
            {
                fw.write(base64.encodeToString(result));
            }
            try(var fw = new FileWriter("iv.txt"))
            {
                fw.write(base64.encodeToString(iv));
            }
        }
    }

    // 5. Step 5: Encrypt with 3DES for Archival
    public static void encryptWith3DES(String filename, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "DESede");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        try(var fis = new FileInputStream("src/safecorp_random_messages/"+filename)) {
            byte[] content = fis.readAllBytes();
        try(var fos = new FileOutputStream("archived.sec"))
            {
                fos.write(cipher.doFinal(content));
            }
        }
    }

    // 6. Step 6: Apply Cyclic Bitwise Shift
    public static void applyCyclicShift(String filename) throws IOException {
        try(var fis = new FileInputStream(filename))
        {
            var allBytes = fis.readAllBytes();
            System.out.println();
            try(var fos = new FileOutputStream("obfuscated.txt")) {

                for (var b : allBytes) {
                    String binaryRep = Integer.toBinaryString(b);
                    String fullBinaryRep = String.format("%1$" + 8 + "s", binaryRep).replace(' ', '0');
                    System.out.printf("Initial bit rep: %s ---- %s hex rep%n", fullBinaryRep, Integer.toHexString(b));
                    StringBuffer sb = new StringBuffer();
                    for (int i = 2; i < fullBinaryRep.length(); i++) {
                        sb.append(fullBinaryRep.charAt(i));
                    }
                    sb.append(fullBinaryRep.charAt(0));
                    sb.append(fullBinaryRep.charAt(1));
                    String shiftedByteRep = sb.toString();
                    int parsedInt = Integer.parseInt(shiftedByteRep, 2);
                    System.out.printf("Rotated bit rep: %s ---- %02x hex rep%n%n", shiftedByteRep, parsedInt);
                    fos.write(parsedInt);
                }
            }
        }
    }

    public static void main(String[] args) {

        String hash = "FECA0B0813150BADA25B66C0E75E1F16CC8867138737EC17FAB38AD32D222FE6"; //copy it from the given Excel file
        String sharedSecret = "S5P?bMM!W]rO"; //copy it from the given Excel file
        int noIterations = 80383; //copy it from the given Excel file

        try {
            // 1. Step 1
            String filename = findFile(hash);

            // 2. Step 2: Generate HMAC for Authentication
            generateHMAC(filename, sharedSecret);

            int keySize = 128;
            byte[] key;
            // 3. Step 3: Derive Key with PBKDF2
            key = deriveKeyWithPBKDF2(sharedSecret, noIterations, keySize);

            // 4. Step 4: Encrypt File with AES and Save IV
            encryptFileWithAES(filename, key);

            // 5. Step 5: Encrypt with 3DES for Archival
            keySize = 192;
            key = deriveKeyWithPBKDF2(sharedSecret, noIterations, keySize);
            encryptWith3DES(filename, key);

            // 6. Step 6: Apply Cyclic Bitwise Shift
            applyCyclicShift("encrypted.txt");

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
