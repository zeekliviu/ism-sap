package homeworks.zecheru_liviu_ioan.pgp;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class PGP {
    static final String path = "src/homeworks/zecheru_liviu_ioan/pgp/";
    static final String certName = "SimplePGP_ISM.cer";
    static final String digSignAlgName = "SHA512withRSA";
    static final String encAlgName = "AES";
    static final String encMode = "ECB";
    static final String encPadding = "PKCS5Padding";
    static final int encKeySize = 128;
    static final String certFactoryName = "X.509";
    static final String sigExt = ".signature";
    static final String txtExt = ".txt";
    static final String responseFileName = "response.txt";
    static final String encKeyFileName = "aes_key.sec";
    static final String keyPairGenAlg = "RSA";
    static final String sigFileName = "signature.ds";
    static final String keyStoreName = "liviuioanzecheru_keystore.jks";
    static final String keyPairName = "liviuioanzecheru_keypair";
    static final String storePass = "st0re_p4ssw0rd";
    static final String keyPass = "k3y_p4ssw0rd";
    static final String exportCertName = "Liviu-Ioan_ZECHERU.cer";

    static final String[] commandForGeneratingKeyPair = {
            "keytool",
            "-genkeypair",
            "-alias", keyPairName,
            "-keyalg", "RSA",
            "-keysize", "2048",
            "-validity", "365",
            "-storetype", "JKS",
            "-keystore", String.format("%s%s", path, keyStoreName),
            "-dname", "CN=Liviu-Ioan ZECHERU, OU=ASE, O=CSIE, L=Sector 4, ST=Municipiul Bucuresti, C=RO",
            "-storepass", storePass,
            "-keypass", keyPass
    };

    static final String[] commandForGeneratingCertificate = {
            "keytool",
            "-export",
            "-alias", keyPairName,
            "-file", String.format("%s%s", path, exportCertName),
            "-storetype", "JKS",
            "-keystore", String.format("%s%s", path, keyStoreName),
            "-storepass", storePass
    };

    static final String[] commandForDeletingKeyPair = {
            "keytool",
            "-delete",
            "-alias", keyPairName,
            "-keystore", String.format("%s%s", path, keyStoreName),
            "-storepass", storePass
    };


    static void verifyMessage() throws Exception {
        var sigFiles = new File(path).listFiles((dir, name) -> name.endsWith(".signature"));
        try(var fis1 = new FileInputStream(String.format("%s%s", path, certName))) {
            var factory = CertificateFactory.getInstance(certFactoryName);
            var certificate = (X509Certificate)factory.generateCertificate(fis1);
            var signature = Signature.getInstance(digSignAlgName);
            signature.initVerify(certificate);
            for (File sigFile : sigFiles) {
                try(var fis2 = new FileInputStream(sigFile.getAbsolutePath().replace(sigExt, txtExt)))
                {
                    var buffer = fis2.readAllBytes();
                    signature.update(buffer);
                    try(var fis3 = new FileInputStream(sigFile))
                    {
                        var sigContent = fis3.readAllBytes();
                        var verified = signature.verify(sigContent);
                        System.out.printf("File corresponding to %s is %s.\n", sigFile.getName(), verified ? "authentic" : "not authentic");
                    }
                }
            }
        }
    }

    static void doTheRest() throws Exception
    {
        var keygen = KeyGenerator.getInstance(encAlgName);
        keygen.init(encKeySize);
        var encKey = keygen.generateKey();
        var cipher = Cipher.getInstance(String.format("%s/%s/%s", encAlgName, encMode, encPadding));
        cipher.init(Cipher.ENCRYPT_MODE, encKey);
        //encrypt the response file
        try(var fis = new FileInputStream(String.format("%s%s", path, responseFileName)))
        {
            var buffer = fis.readAllBytes();
            var encrypted = cipher.doFinal(buffer);
            try(var fos = new FileOutputStream(String.format("%s%s", path, responseFileName.replace(txtExt, ".sec")))
            )
            {
                fos.write(encrypted);
            }
        }
        //encrypt the key
        try(var fis = new FileInputStream(String.format("%s%s", path, certName)))
        {
            cipher = Cipher.getInstance(keyPairGenAlg);
            var certificate = (X509Certificate)CertificateFactory.getInstance(certFactoryName).generateCertificate(fis);
            cipher.init(Cipher.ENCRYPT_MODE, certificate);
            try(var fos = new FileOutputStream(String.format("%s%s", path, encKeyFileName))
            )
            {
                var toWrite = cipher.doFinal(encKey.getEncoded());
                fos.write(toWrite);

                // try to delete the key pair (for second run)
                var pb = new ProcessBuilder(commandForDeletingKeyPair);
                pb.redirectErrorStream(true);
                var process = pb.start();

                try (var stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String s;
                    while ((s = stdInput.readLine()) != null) {
                        System.out.println(s);
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    System.out.println("No existing key pair to delete or another error occurred (if error is unrelated to a missing key, handle accordingly).");
                }

                // generate the key pair in order to sign the response file
                pb = new ProcessBuilder(commandForGeneratingKeyPair);
                pb.redirectErrorStream(true);
                process = pb.start();

                try(var stdInput = new BufferedReader(new InputStreamReader(process.getInputStream())))
                {
                    String s;
                    while((s = stdInput.readLine()) != null)
                    {
                        System.out.println(s);
                    }
                }

                // export the certificate
                pb = new ProcessBuilder(commandForGeneratingCertificate);
                pb.redirectErrorStream(true);
                process = pb.start();

                try(var stdInput = new BufferedReader(new InputStreamReader(process.getInputStream())))
                {
                    String s;
                    while((s = stdInput.readLine()) != null)
                    {
                        System.out.println(s);
                    }
                }

                // sign the response file
                var ks = KeyStore.getInstance("JKS");
                try(var fis2 = new FileInputStream(String.format("%s%s", path, keyStoreName)))
                {
                    ks.load(fis2, storePass.toCharArray());
                    var privateKey = ks.getKey(keyPairName, keyPass.toCharArray());
                    var signature = Signature.getInstance(digSignAlgName);
                    signature.initSign((PrivateKey) privateKey);
                    try(var fis3 = new FileInputStream(String.format("%s%s", path, responseFileName)))
                    {
                        var buffer = fis3.readAllBytes();
                        signature.update(buffer);
                        var signed = signature.sign();
                        try(var fos2 = new FileOutputStream(String.format("%s%s", path, sigFileName)))
                        {
                            fos2.write(signed);
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception{
        verifyMessage();
        doTheRest();
    }
}
