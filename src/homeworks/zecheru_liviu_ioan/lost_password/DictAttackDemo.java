package homeworks.zecheru_liviu_ioan.lost_password;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DictAttackDemo {

    static final String salt = "ismsap";
    static final String crackedHash = "81272d199f167e7ba4ec2a27d33ab72195791643820409d993e6d7b93d28498e";

    static String bytesToHex(byte[] bytes)
    {
        StringBuilder hexString = new StringBuilder(2*bytes.length);
        for(byte b: bytes)
        {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1)
            {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


    static void tryBreach(String[] passwords, AtomicBoolean found) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        MessageDigest sha1 = MessageDigest.getInstance("SHA-256");

        for (String password : passwords) {
            if(found.get())
            {
                return;
            }
            String saltedPassword = salt + password;
            byte[] md5Hash = md5.digest(saltedPassword.getBytes());
            byte[] sha1Hash = sha1.digest(md5Hash);
            String sha1HashString = bytesToHex(sha1Hash);
            if (sha1HashString.equals(crackedHash)) {
                System.out.printf("Password found: %s%n", password);
                found.set(true);
                return;
            }
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

        try(var br = new BufferedReader(new FileReader("src/homeworks/zecheru_liviu_ioan/lost_password/ignis-10M.txt")))
        {
            var lines = br.lines().toArray(String[]::new);

            AtomicBoolean found = new AtomicBoolean(false);

            long startTime = System.currentTimeMillis();

            List<Thread> threadPool = getThreads(lines, found);

            for (Thread t : threadPool) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            long endTime = System.currentTimeMillis();

            System.out.printf("Time elapsed: %d ms%n", endTime - startTime);
        }
    }

    private static List<Thread> getThreads(String[] lines, AtomicBoolean found) {
        List<Thread> threadPool = new ArrayList<>();
        for (int i = 0; i < lines.length; i += 1_000_000) {
            final int start = i;
            final int end = Math.min(i + 1_000_000, lines.length);
            Thread t = new Thread(() -> {
                try {
                    tryBreach(Arrays.copyOfRange(lines, start, end), found);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            });
            threadPool.add(t);
            t.start();
        }
        return threadPool;
    }
}
