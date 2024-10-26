package lab1.lfsr;

public class Main {
    //implements a simple LFSR with a 32-bit register and TAP sequence x^31+x^7+x^5+x^3+x^2+x+1

    // implementation - integer vs byte array of 4 values
    //integer implementation

    static void printRegister(int register)
    {
        System.out.printf("The register is %s%n or 0x%s%n", Integer.toBinaryString(register), Integer.toHexString(register));
    }

    static void printByteArray(byte[] array)
    {
        for (byte b : array) {
            System.out.printf("0x%02x ", b);
        }
    }

    static int initRegister(byte[] initialValue)
    {
        if(initialValue.length!=4)
        {
            System.out.println("Wrong initial value. 4 bytes needed.");
            return 0;
        }
        int result = 0;
        for(int i = 3; i >= 0; i--)
        {
            result |= ((initialValue[3-i] & 0xff) << (i * 8));
        }
        return result;
    }

    static byte applyTapSequence(int register) {
        // TAP sequence x^31+x^7+x^5+x^3+x^2+x+1
        byte[] index = {31,7,5,3,2,1,0};
        byte result = 0;

        for(var b: index)
        {
            result ^= (byte) (((1 << b) & register) >>> b);
        }

        return result;
    }

    static byte getLeastSignificantBit(int register)
    {
        return (byte) (register & 1);
    }

    static int shiftAndInsertTapBit(int register, byte tapBit)
    {
        register >>>= 1;
        register |= (tapBit << 31);
        return register;
    }


    public static void main(String[] args) {

        // the register
        int register = 0;
        // the initial seed
        byte[] seed = {(byte)0b10101010, (byte)0b11110000, (byte)0b00001111, (byte)0b01010101};
        register = initRegister(seed);
        printRegister(register);
    }
}
