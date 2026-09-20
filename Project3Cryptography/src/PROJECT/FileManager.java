package PROJECT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class FileManager {

    public static String readKey(String f) {
        try {
            File file = new File(f);
            Scanner read = new Scanner(file);
            String key = read.nextLine();
            read.close();
            return key;
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
            return null;
        }
    }

    public static String readData(String f) {
        try {
            File file = new File(f);
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = fis.readAllBytes();
            fis.close();
            return new String(bytes);
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
            return null;
        }
    }

    public static void writeToFile(String data, String filePath) {
        try {
            PrintWriter writer = new PrintWriter(filePath);
            writer.print(data);
            writer.close();
        } catch (Exception e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    public static byte[] readFileBinary2(String filePath) {
        try {
            FileInputStream f = new FileInputStream(filePath);
            byte[] bytes = f.readAllBytes();
            f.close();
            return bytes;
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
            return null;
        }
    }

    public static void writeBinaryFile2(byte[] data, String filePath) {
        try {
            FileOutputStream f = new FileOutputStream(filePath);
            f.write(data);
            f.close();
        } catch (Exception e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}