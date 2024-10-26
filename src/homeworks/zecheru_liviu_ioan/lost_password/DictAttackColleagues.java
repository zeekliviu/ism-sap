package homeworks.zecheru_liviu_ioan.lost_password;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DictAttackColleagues {
    static final String salt = "ismsap";

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


    static void tryBreach(String[] passwords, String name, String crackedHash, AtomicBoolean found, BufferedWriter bw) throws NoSuchAlgorithmException {
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
                try {
                    bw.write(name + "," + password + "\n");
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

        try(var br = new BufferedReader(new FileReader("src/homeworks/zecheru_liviu_ioan/lost_password/ignis-10M.txt")))
        {
            var lines = br.lines().toArray(String[]::new);

            try(var br2 = new BufferedReader(new FileReader("src/homeworks/zecheru_liviu_ioan/lost_password/colleagues.txt")))
            {
                try(var bw = new BufferedWriter(new FileWriter("src/homeworks/zecheru_liviu_ioan/lost_password/breached.txt"))) {
                    String line;
                    while ((line = br2.readLine()) != null) {
                        String name = line.split(",")[0];
                        String crackedHash = line.split(",")[1];
                        AtomicBoolean found = new AtomicBoolean(false);
                        List<Thread> threadPool = getThreads(lines, name, crackedHash, found, bw);
                        for (Thread t : threadPool) {
                            try {
                                t.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

        }
    }

    private static List<Thread> getThreads(String[] lines, String name, String crackedHash, AtomicBoolean found, BufferedWriter bw) {
        List<Thread> threadPool = new ArrayList<>();
        for (int i = 0; i < lines.length; i += 1_000_000) {
            final int start = i;
            final int end = Math.min(i + 1_000_000, lines.length);
            Thread t = new Thread(() -> {
                try {
                    tryBreach(Arrays.copyOfRange(lines, start, end), name, crackedHash, found, bw);
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
