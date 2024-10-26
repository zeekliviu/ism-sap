package lab1.strings_and_bytes;

public class Main {

    static String getByteUnsignedHexRepresentation(byte value)
    {
        String hex = Integer.toHexString(Byte.toUnsignedInt(value));
        return hex.length() == 1 ? String.format("0%s", hex) : hex;
    }

    static String byteArrayToHex(byte[] values) {
        StringBuilder sb = new StringBuilder();
        for(var b : values)
        {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String file1 = "Keys.txt";
        String file2 = new String("Keys.txt");
        if(file1.equals(file2)) // not == because it compares the reference
        {
            System.out.println("They are equal.");
        }
        else {
            System.out.println("They are different.");
        }

        int vb = 10;
        Integer intVb = 100;
        Integer intVb2 = 100;

        if(intVb == intVb2) // == compares the reference, but for 1 byte integers, it compares the value because of the constant integer pool
        {
            System.out.println("They are equal.");
        }
        else {
            System.out.println("They are different.");
        }

        // converting numbers to strings and vice versa
        int value = 33;
        System.out.println(Integer.toBinaryString(value));
        System.out.printf("0x%s%n",Integer.toHexString(value));

        byte smallValue = 127;
        System.out.println(Integer.toBinaryString(smallValue));
        System.out.printf("0x%s%n",Integer.toHexString(smallValue));

        int smallValue2 = -23;

        // from string to numbers
        Integer initValue = Integer.parseInt("23", 10);
        System.out.println(initValue);
        initValue = Integer.parseInt("23", 16);
        System.out.println(initValue);
        initValue = Integer.parseInt("10111", 2);
        System.out.println(initValue);

        System.out.println(Integer.toHexString(Byte.toUnsignedInt((byte)smallValue2)));

        System.out.println(Integer.toBinaryString(smallValue2));
        System.out.printf("0x%s%n",Integer.toHexString(smallValue2));

        byte[] hash = {(byte)23, (byte)-23, (byte)10, (byte)5};
        //wrong way
        String hashHexString = "";
        for(int i=0; i<hash.length; i++)
        {
            hashHexString += Integer.toHexString(hash[i]);
        }
        System.out.printf("The hash is %s%n", hashHexString);
        // correct way
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<hash.length; i++)
        {
            sb.append(getByteUnsignedHexRepresentation(hash[i]));
        }
        // even better
        System.out.println(byteArrayToHex(hash));
        System.out.printf("The hash is %s%n", sb);
        for(byte b : hash)
        {
            System.out.printf("0x%s ", Integer.toHexString(Byte.toUnsignedInt(b)));
        }
    }
}