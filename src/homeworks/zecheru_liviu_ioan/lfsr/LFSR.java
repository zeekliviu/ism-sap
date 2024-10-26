package homeworks.zecheru_liviu_ioan.lfsr;

import java.util.BitSet;

public class LFSR {

    static final int BYTE_LENGTH_1 = 20;
    static final int BYTE_LENGTH_2 = 50;

    static void printRegister(int register) {
        System.out.printf("The register is %s%n or 0x%s%n", Integer.toBinaryString(register), Integer.toHexString(register));
    }

    static void printByteArray(byte[] array) {
        for (byte b : array) {
            System.out.printf("0x%02x ", b);
        }
    }

    static int initRegister(byte[] initialValue) {
        if (initialValue.length != 4) {
            System.out.println("Wrong initial value. 4 bytes needed.");
            return 0;
        }
        int result = 0;
        for (int i = 3; i >= 0; i--) {
            result |= ((initialValue[3 - i] & 0xff) << (i * Byte.SIZE));
        }
        return result;
    }

    static BitSet initBitSet(byte[] initialValue) {
        if (initialValue.length != 4) {
            System.out.println("Wrong initial value. 4 bytes needed.");
            return null;
        }
        BitSet result = new BitSet(Integer.SIZE);
        for (int i = 0; i < Integer.SIZE; i++) {
            if ((initialValue[3 - i / Byte.SIZE] & (1 << (i % Byte.SIZE))) != 0) {
                result.set(i);
            }
        }
        return result;
    }

    static byte applyTapSequence(int register) {
        byte[] index = {31, 7, 5, 3, 2, 1, 0};
        byte result = 0;
        for (var b : index) {
            result ^= (byte) (((1 << b) & register) >>> b);
        }
        return result;
    }

    static byte applyTapSequence(BitSet register) {
        byte[] index = {31, 7, 5, 3, 2, 1, 0};
        byte result = 0;
        for (var b : index) {
            result ^= (byte) (register.get(b) ? 1 : 0);
        }
        return result;
    }

    static byte getLeastSignificantBit(BitSet register) {
        return (byte) (register.get(0) ? 1 : 0);
    }

    static byte getLeastSignificantBit(int register) {
        return (byte) (register & 1);
    }

    static int shiftAndInsertTapBit(int register, byte tapBit) {
        register >>>= 1;
        register |= (tapBit << (Integer.SIZE - 1));
        return register;
    }

    static BitSet shiftAndInsertTapBit(BitSet register, byte tapBit) {
        BitSet newRegister = new BitSet(Integer.SIZE);

        for (int i = 1; i < Integer.SIZE; i++) {
            if (register.get(i)) {
                newRegister.set(i - 1);
            }
        }

        newRegister.set(Integer.SIZE - 1, tapBit == 1);

        return newRegister;
    }

    static int fullStep(int register) {
        byte tapBit = applyTapSequence(register);
        return shiftAndInsertTapBit(register, tapBit);
    }

    static BitSet fullStep(BitSet register) {
        byte tapBit = applyTapSequence(register);
        return shiftAndInsertTapBit(register, tapBit);
    }

    static byte[] generatePseudoRandomBytes(int register, int size) {
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            byte generatedByte = 0;
            for (int bit = 0; bit < Byte.SIZE; bit++) {
                byte pseudoBit = getLeastSignificantBit(register);
                register = fullStep(register);
                generatedByte |= (byte) (pseudoBit << (Byte.SIZE - 1 - bit));
            }
            result[i] = generatedByte;
        }
        return result;
    }

    static byte[] generatePseudoRandomBytes(BitSet register, int size) {
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            byte generatedByte = 0;
            for (int bit = 0; bit < Byte.SIZE; bit++) {
                byte pseudoBit = getLeastSignificantBit(register);
                register = fullStep(register);
                generatedByte |= (byte) (pseudoBit << (Byte.SIZE - 1 - bit));
            }
            result[i] = generatedByte;
        }
        return result;
    }

    public static void main(String[] args) {
        byte[] seed = {(byte) 0b10101010, (byte) 0b11110000, (byte) 0b00001111, (byte) 0b01010101};

        int register = initRegister(seed);
        printRegister(register);
        byte[] randomBytes = generatePseudoRandomBytes(register, BYTE_LENGTH_1);
        System.out.printf("Generated %d pseudo-random bytes (int-based): ", BYTE_LENGTH_1);
        printByteArray(randomBytes);

        System.out.println();

        BitSet bitSetRegister = initBitSet(seed);
        byte[] randomBytesBitSet = generatePseudoRandomBytes(bitSetRegister, BYTE_LENGTH_1);
        System.out.printf("Generated %d pseudo-random bytes (BitSet-based): ", BYTE_LENGTH_1);
        printByteArray(randomBytesBitSet);

        System.out.println();

        randomBytes = generatePseudoRandomBytes(register, BYTE_LENGTH_2);
        System.out.printf("Generated %d pseudo-random bytes (int-based): ", BYTE_LENGTH_2);
        printByteArray(randomBytes);

        System.out.println();

        randomBytesBitSet = generatePseudoRandomBytes(bitSetRegister, BYTE_LENGTH_2);
        System.out.printf("Generated %d pseudo-random bytes (BitSet-based): ", BYTE_LENGTH_2);
        printByteArray(randomBytesBitSet);

        System.out.println();
    }
}
