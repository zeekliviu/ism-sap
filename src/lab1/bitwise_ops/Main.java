package lab1.bitwise_ops;

public class Main {
    public static void main(String[] args) {
        byte value = 15;
        System.out.printf("Value is %s%n", value);
        value = 0b00001111;
        System.out.printf("Value is %s%n", value);
        value = 0x0f;
        System.out.printf("Value is %s%n", value);

        value = 1 << 3 | 1 << 2 | 1 << 1 | 1;
        System.out.printf("Value is %s%n", value);

        value = 8;
        value = (byte)(value<<1); // multiply by 2
        System.out.printf("Value is %s%n", value);
        value = (byte)(value >> 1); // division by 2
        System.out.printf("Value is %s%n", value);

        value = 65;
        value = (byte)(value<<1);
        System.out.printf("Value is %s%n", value);

        value = -1;
        System.out.printf("Value is %02x%n", value);
        value = (byte)(value >> 1);
        System.out.printf("Value is %s%n", value);
        value = (byte)(value >>> 1); // DOES NOT WORK on BYTES
        System.out.printf("Value is %s%n", value);

        int value2 = -1;
        value2 = value2 >> 1; // shifts the bit sign AND preserves the value sign
        System.out.printf("Value2 is %s%n", value2);

        int value3 = -1;
        value3 = value3 >>> 1; // shifts the bit sign but does not preserver the value sign
        System.out.printf("Value3 is %s%n", value3);

        //checking for bits
        // check if a byte has the 3rd bit 1 (left to right, 1st has the index 1)
        byte anyValue = 39;
        // use a bit mask
        byte bitMask = 1 << 5; //0b00100000;
        byte result = (byte)(anyValue & bitMask); // possible values: 0 or 1
        if(result == 0)
        {
            System.out.println("3rd bit is 0");
        }
        else {
            System.out.println("3rd bit is 1");
        }
    }
}