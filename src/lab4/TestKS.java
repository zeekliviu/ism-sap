package lab4;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.io.File;
import java.io.FileInputStream;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class TestKS {
    static void printHex(byte[] bytes) {
        for (byte b : bytes) {
            System.out.printf("%02x", b);
        }
        System.out.println();
    }

    static KeyStore getKeyStore(String ksFileName, String ksPassword) throws Exception
    {
        File ksFile = new File(ksFileName);
        if(!ksFile.exists())
        {
            throw new UnsupportedOperationException("KeyStore file not found");
        }

        try(var fis = new FileInputStream(ksFile))
        {
            KeyStore ks = KeyStore.getInstance("jks");
            ks.load(fis, ksPassword.toCharArray());
            return ks;
        }
    }

    static void printKSContent(KeyStore ks) throws Exception
    {
        if(ks!=null)
        {
            System.out.println("KeyStore content:");
            var enumeration = ks.aliases();
            while(enumeration.hasMoreElements())
            {
                String alias = enumeration.nextElement();
                System.out.println("Alias: " + alias);
                if(ks.isKeyEntry(alias))
                {
                    System.out.println("\t is a key pair");
                }
                else if(ks.isCertificateEntry(alias))
                {
                    System.out.println("\t is a public key");
                }
            }
        }
    }

    static PublicKey getPublicKey(KeyStore ks, String alias) throws Exception
    {
        if(!ks.containsAlias(alias))
        {
            throw new UnsupportedOperationException("Alias not found");
        }
        return ks.getCertificate(alias).getPublicKey();
    }

    static PrivateKey getPrivateKey(KeyStore ks, String alias, String password) throws Exception
    {
        if(ks!=null && ks.isKeyEntry(alias)) {
            return (PrivateKey) ks.getKey(alias, password.toCharArray());
        }
        else
        {
            throw new UnsupportedOperationException("Alias not found or not a key pair");
        }
    }

    static PublicKey getPublicFromX509(String fileName) throws Exception {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new UnsupportedOperationException("File not found");
        }
        try(var fis = new FileInputStream(file))
        {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            var cert = (X509Certificate)cf.generateCertificate(fis);
            return cert.getPublicKey();
        }
    }

    static byte[] getSymmetricRandomKey(int bitsNo, String algorithm) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
        keyGen.init(bitsNo);
        return keyGen.generateKey().getEncoded();
    }

    static byte[] encrypt(Key key, byte[] input) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input);
    }

    static byte[] decrypt(Key key, byte[] input) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(input);
    }

    static byte[] getDigitalSignature(String fileName, Key privateKey) throws Exception
    {
        File inputFile = new File(fileName);
        if(!inputFile.exists())
        {
            throw new UnsupportedOperationException("File not found");
        }
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign((PrivateKey)privateKey);
        try(var fis = new FileInputStream(inputFile))
        {
            // process the entire file on one round
            byte[] entireFile = fis.readAllBytes();
            signature.update(entireFile);
            return signature.sign();
        }
    }

    static boolean isValidSignature(String fileName, byte[] signature, Key publicKey) throws Exception
    {
        File inputFile = new File(fileName);
        if(!inputFile.exists())
        {
            throw new UnsupportedOperationException("File not found");
        }
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify((PublicKey)publicKey);
        try(var fis = new FileInputStream(inputFile))
        {
            // process the entire file on one round
            byte[] entireFile = fis.readAllBytes();
            sig.update(entireFile);
            return sig.verify(signature);
        }
    }

    public static void main(String[] args) throws Exception{
        var ks = getKeyStore("src/lab4/ismkeystore.ks", "passks");
        printKSContent(ks);

        //get public key
        PublicKey ismasero = getPublicKey(ks, "ismasero");
        System.out.println("ISM.ASE.RO public key: ");
        printHex(ismasero.getEncoded());

        PublicKey ismkey1 = getPublicKey(ks, "ismkey1");
        System.out.println("ISM.KEY1 public key: ");
        printHex(ismkey1.getEncoded());

        PrivateKey ism1Priv = getPrivateKey(ks, "ismkey1", "passks");
        System.out.println("ISM.KEY1 private key: ");
        printHex(ism1Priv.getEncoded());

        //get public key from X509 certificate
        PublicKey ismcert = getPublicFromX509("src/lab4/ISMCertificateX509.cer");
        System.out.println("ISM.ASE.RO public key from X509: ");
        printHex(ismcert.getEncoded());

        //generate symmetric key
        System.out.println("Random AES key: ");
        byte[] randomAESKey = getSymmetricRandomKey(128, "AES");
        printHex(randomAESKey);

        byte[] encAESKey = encrypt(ismkey1, randomAESKey);
        System.out.println("Encrypted AES key: ");
        printHex(encAESKey);

        byte[] initialAESKey = decrypt(ism1Priv, encAESKey);
        System.out.println("Decrypted AES key: ");
        printHex(initialAESKey);

        byte[] msgSignature = getDigitalSignature("src/lab4/hello.txt", ism1Priv);
        System.out.println("Signature: ");
        printHex(msgSignature);

        boolean isValid = isValidSignature("src/lab4/hello.txt", msgSignature, ismcert);
        System.out.println("Is valid signature: " + isValid);
    }
}
