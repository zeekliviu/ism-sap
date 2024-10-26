package exams.july;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Exam {

    static File Cerinta1() throws Exception {
        var dir = new File("src/exams/july/system32");
        var files = dir.listFiles();
        Map<String, String> fileDataList = new HashMap<>();
        try(var br = new BufferedReader(new FileReader("src/exams/july/sha2Fingerprints.txt")))
        {
            String fileName;
            String refSha;
            while((fileName = br.readLine())!=null && (refSha = br.readLine())!= null)
            {
                fileDataList.put(refSha, fileName);
            }
        }
        var base64Encoder = Base64.getEncoder();
        var sha256 = MessageDigest.getInstance("SHA-256");
        for(var f: files)
        {
            try(var fis = new FileInputStream(f))
            {
                var buffer = new byte[8];
                int readBytes;
                while((readBytes = fis.read(buffer))!=-1)
                {
                    sha256.update(buffer, 0, readBytes);
                }
                var sha256Base64Encoded = base64Encoder.encodeToString(sha256.digest());
                if(!fileDataList.containsKey(sha256Base64Encoded))
                {
                    System.out.printf("The file name is: %s%n", f.getName());
                    return f;
                }
            }
        }
        return null;
    }

    static void Cerinta2() throws Exception {
        File result = Cerinta1();
        try(var fis = new FileInputStream(result))
        {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] pass = fis.readAllBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(pass, "AES");
            byte[] iv = new byte[cipher.getBlockSize()];
            int length = iv.length;
            iv[length-1] = (byte)0x17;
            iv[length-2] = (byte)0x14;
            iv[length-3] = (byte)0x02;
            iv[length-4] = (byte)0x04;
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] buffer = new byte[cipher.getBlockSize()];
            int readBytes;
            StringBuffer sb = new StringBuffer();
            try(var fis2 = new FileInputStream("src/exams/july/financialdata.enc"))
            {
                try(var bw = new BufferedWriter(new FileWriter("src/exams/july/financialdata.txt"))) {
                    while ((readBytes = fis2.read(buffer)) != -1) {
                        String intermediate = new String(cipher.update(buffer, 0, readBytes));
                        bw.write(intermediate);
                        sb.append(intermediate);
                    }
                    String finalString = new String(cipher.doFinal());
                    bw.write(finalString);
                    sb.append(finalString);
                }
            }
        }
    }

    static void Cerinta3() throws Exception {
        Cerinta2();
        try(var br = new BufferedReader(new FileReader("src/exams/july/financialdata.txt"))) {
            String IBAN = br.readLine();
            try(var bw = new FileWriter("src/exams/july/myresponse.txt"))
            {
                bw.write(IBAN);
            }
        }
        try(var fis = new FileInputStream("src/exams/july/myresponse.txt"))
        {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try(var kss = new FileInputStream("src/exams/july/mykeystore.jks"))
            {
                keyStore.load(kss, "password".toCharArray());
            }

            PrivateKey privateKey = (PrivateKey) keyStore.getKey("mykey", "password".toCharArray());

            byte[] buffer = fis.readAllBytes();

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(buffer);

            byte[] digitalSignature = signature.sign();

            try(var fos = new FileOutputStream("src/exams/july/DataSignature.ds"))
            {
                fos.write(digitalSignature);
            }
        }
    }

    public static void main(String[] args) throws Exception {
    Cerinta3();
    }
}
