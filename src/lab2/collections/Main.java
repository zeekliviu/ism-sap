package lab2.collections;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // collections types
        // 1. List - variable size arrays, ordered by insertion
        // 2. Set - variable size arrays, unique elements
        // 3. Map - key-value pairs, unique keys

        // Lists
        List<String> files = new ArrayList<>();
        files.add("Keys.txt");
        files.add("Passwords.txt");
        files.add("Users.txt");

        for(String file : files) {
            System.out.printf("File: %s\n", file);
        }

        // Sets

        Set<String> usernames = new HashSet<>();
        usernames.add("admin");
        usernames.add("admin");
        usernames.add("user");

        for(String username : usernames) {
            System.out.printf("Username: %s\n", username);
        }

        // Maps

        Map<Integer, String> users = new HashMap<>();

        users.put(10, "Alice");
        users.put(10, "Bob");
        users.put(15, "John");

        String user = users.get(19);
        if(user != null) {
            System.out.printf("User: %s\n", user);
        }
        else {
            System.out.println("User not found");
        }

        // Test shallow copy
        List<Byte> key = Arrays.asList((byte) 0xA4, (byte) 0x10, (byte) 0x2F, (byte) 0x22);
        Certificate certISM = new Certificate("ISM", key, 1024, "ism.ase.ro");

        certISM.print();

        Certificate certPortalISM = new Certificate("PortalISM", key, 1024, "portal.ism.ase.ro");

        certPortalISM.print();

        key.set(0, (byte) 0x00);

        certISM.print();
        certPortalISM.print();

        //  BitSet
        BitSet bitSet = new BitSet(32);
        bitSet.set(0);
        bitSet.set(30);
        BitSet register = BitSet.valueOf(new byte[]{(byte) 0xa4, (byte) 0x10, (byte) 0x2f, (byte) 0x22});
        for(int i = 0; i < register.length(); i++) {
            System.out.printf("%d ", register.get(i) ? 1 : 0);
        }

        System.out.println("Register: ");
        for(var b: register.toByteArray()) {
            System.out.printf("%02x ", b);
        }
    }
}
