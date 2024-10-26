package lab2.files;

import java.io.*;

public class Main {
    static void listFolder(File folder, String indentation) {
        File[] items = folder.listFiles();
        if(folder.isDirectory() && folder.exists())
        {
            for (File item : items) {
                System.out.printf("%s%s %s\n", indentation, item.isDirectory() ? "DIR" : "FILE", item.getName());
                if (item.isDirectory()) {
                    listFolder(item, indentation + "\t");
                }
            }
        }
    }
    public static void main(String[] args) throws IOException {
        File repository = new File("./");
        File[] items = repository.listFiles();
        if(repository.isDirectory() && repository.exists())
        {
            for (File item : items) {
            System.out.printf("%s %s\n", item.isDirectory() ? "DIR" : "FILE", item.getName());
            }
        }

        listFolder(repository, "");

        File msgFile = new File("Message.txt");
        if(!msgFile.exists())
        {
            boolean created = msgFile.createNewFile();
        }
        else
        {
            FileWriter fileWriter = new FileWriter(msgFile, true);
            PrintWriter writer = new PrintWriter(fileWriter);
            writer.println("This is a secret message.");
            writer.println("Don't tell anyone.");
            writer.close();
            System.out.println("File found");
        }

        // reading from text files
        try(var reader = new BufferedReader(new FileReader(msgFile)))
        {
            String line;
            while((line = reader.readLine()) != null)
            {
                System.out.println(line);
            }
        }

        // binary files
        File binaryFile = new File("binaryFile.bin");
        if(!binaryFile.exists())
        {
            binaryFile.createNewFile();
        }

        float floatValue = 23.5f;
        double doubleValue = 10;
        int intValue = 10;
        boolean flag = true;
        String text = "Hello, World!";
        byte[] bytes = new byte[] {(byte)0xff, (byte)0xfe};

        FileOutputStream fos = new FileOutputStream(binaryFile);
        DataOutputStream dos = new DataOutputStream(fos);

        dos.writeFloat(floatValue);
        dos.writeDouble(doubleValue);
        dos.writeInt(intValue);
        dos.writeBoolean(flag);
        dos.writeUTF(text);
        dos.write(bytes);

        dos.close();

        // read binary file
        FileInputStream fis = new FileInputStream(binaryFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        DataInputStream dis = new DataInputStream(bis);

        floatValue = dis.readFloat();
        doubleValue = dis.readDouble();
        intValue = dis.readInt();
        flag = dis.readBoolean();
        text = dis.readUTF();
        bytes = dis.readAllBytes(); // we know that the array is the last element in the file

        dis.close();

        //print the values
        System.out.println("Float: " + floatValue);
        System.out.println("Double: " + doubleValue);
        System.out.println("Int: " + intValue);
        System.out.println("Boolean: " + flag);
        System.out.println("Text: " + text);
        System.out.printf("Bytes: %x %x\n", bytes[0], bytes[1]);

        RandomAccessFile raf = new RandomAccessFile(binaryFile, "r");
        raf.seek(Float.BYTES + Double.BYTES); // jump the float and the double
        int vb = raf.readInt();
        System.out.println("Int: " + vb);
        raf.close();
    }
}
