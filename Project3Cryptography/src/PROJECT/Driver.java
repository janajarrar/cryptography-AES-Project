package PROJECT;
 
import java.io.File;
import java.util.Scanner;
 
public class Driver {
 
    private static Scanner sc = new Scanner(System.in);
    private static int keySize = 128;
    private static String mode = "ECB";
    private static byte[] currentKey = null;
    private static byte[] currentIV = null;
    private static byte[] currentNonce = null;
    private static String lastCiphertextHex = "";
    private static String lastAesFile = "";
 
    public static void main(String[] args) {
        while (true) {
            printMainMenu();
            int choice = readInt();
            try {
            	
                switch (choice) {
                    case 1:
                        setKeySize();
                        break;
                        
                    case 2:
                        keyMenu();
                        break;
                        
                    case 3:
                        keyExpansionMenu();
                        break;
                        
                    case 4:
                        setMode();
                        break;
                        
                    case 5:
                        encryptMenu();
                        break;
                        
                    case 6:
                        decryptMenu();
                        break;
                        
                    case 7:
                        System.out.println("\nSelect file operation:");
                        System.out.println("1. Encrypt file");
                        System.out.println("2. Decrypt file");
                        int fileOp = readInt();
                        
                        switch (fileOp) {
                            case 1:
                                encryptMenu();
                                break;
                                
                            case 2:
                                decryptMenu();
                                break;
                                
                            default:
                                System.out.println("Invalid choice.");
                                break;
                                
                        }
                        break;
                        
                    case 8:
                        sendCiphertextEmail();
                        break;
                        
                    case 9:
                        sendKeyMaterialEmail();
                        break;
                        
                    case 10:
                        System.out.println("Goodbye!");
                        return;
                        
                    default:
                        System.out.println("Invalid option. Try again.");
                }
                
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
 
    private static void printMainMenu() {
        System.out.println("\n--- AES Encryption System ---");
        System.out.println("Key Size : AES-" + keySize);
        System.out.println("Mode     : " + mode);
 
        if (currentKey != null) {
            System.out.println("Key      : Set");
        } else {
            System.out.println("Key      : Not Set");
        }
 
        if (mode.equals("CFB")) {
            if (currentIV != null) {
                System.out.println("IV       : " + bytesToHex(currentIV));
                
            } else {
                System.out.println("IV       : Not Set");
            }
        }
 
        if (mode.equals("OFB")) {
        	
            if (currentNonce != null) {
                System.out.println("Nonce    : " + bytesToHex(currentNonce));
                
            } else {
                System.out.println("Nonce    : Not Set");
                
            }
        }
 
        System.out.println();
        System.out.println("1. Select AES key size (128 or 192 bits)");
        System.out.println("2. Set / Generate Key");
        System.out.println("3. Perform and display key expansion");
        System.out.println("4. Select the cipher mode (ECB, OFB, or CFB)");
        System.out.println("5. Encrypt Plaintext");
        System.out.println("6. Decrypt Ciphertext");
        System.out.println("7. Choose input/output method (console or file)");
        System.out.println("8. Send Ciphertext via Email");
        System.out.println("9. Send AES Key / IV / Nonce via Email");
        System.out.println("10. Exit");
        System.out.print("Enter choice: ");
    }
 
    private static int readInt() {
        try {
        	
            int val = sc.nextInt();
            sc.nextLine();
            return val;
            
        } catch (Exception e) {
            sc.nextLine();
            return -1;
            
        }
    }
 
    private static void setKeySize() {
        System.out.print("Enter key size (128 or 192): ");
        int s = readInt();
        if (s == 128 || s == 192) {
            keySize = s;
            currentKey = null;
            
            System.out.println("Key size set to AES-" + keySize);
        } else {
        	
            System.out.println("Invalid key size! Choose 128 or 192.");
        }
    }
 
    private static void keyMenu() {
        System.out.println("\nKey Options:");
        System.out.println("1. Generate random key");
        System.out.println("2. Enter key manually (hex)");
        System.out.print("Enter choice: ");
        int choice = readInt();
 
        if (choice == 1) {
            currentKey = KeyExpansion.generateRandom(keySize / 8);
            System.out.println("Key generated: " + bytesToHex(currentKey));
 
        } else if (choice == 2) {
            System.out.print("Enter hex key (" + keySize / 4 + " hex characters): ");
            String hex = sc.nextLine().replaceAll("\\s+", "");
 
            if (!isValidHex(hex) || hex.length() != keySize / 4) {
                System.out.println("Invalid key length!");
                return;
            }
 
            currentKey = hexToBytes(hex);
            System.out.println("Key set successfully.");
 
        } else {
            System.out.println("Invalid choice.");
        }
    }
 
    private static void setMode() {
        System.out.println("\nSelect Cipher Mode:");
        System.out.println("1. ECB (Electronic Codebook)");
        System.out.println("2. OFB (Output Feedback)");
        System.out.println("3. CFB (Cipher Feedback)");
        System.out.print("Enter choice: ");
        int m = readInt();
        
        switch (m) {
            case 1:
                mode = "ECB";
                currentIV = null;
                currentNonce = null;
                System.out.println("Mode set to ECB.");
                break;
                
            case 2:
                mode = "OFB";
                currentIV = null;
                generateNonce();
                break;
                
            case 3:
                mode = "CFB";
                currentNonce = null;
                ivMenu();
                break;
                
            default:
                System.out.println("Invalid choice.");
                break;
        }
        
    }
 
    private static void generateNonce() {
        System.out.println("\nNonce Options:");
        System.out.println("1. Generate randomly");
        System.out.println("2. Enter manually (32 hex characters)");
        System.out.print("Enter choice: ");
        int choice = readInt();
        
        if (choice == 1) {
            currentNonce = KeyExpansion.generateRandom(16);
            System.out.println("Generated Nonce (Hex): " + bytesToHex(currentNonce));
            
        } else if (choice == 2) {
            System.out.print("Enter 32 hex characters: ");
            String hex = sc.nextLine().replaceAll("\\s+", "");
            
            if (isValidHex(hex) && hex.length() == 32) {
                currentNonce = hexToBytes(hex);
                System.out.println("Nonce set successfully.");
                
            } else {
                System.out.println("Invalid! Must be exactly 32 hex characters.");
            }
            
        } else {
            System.out.println("Invalid choice.");
        }
    }
 
    private static void ivMenu() {
        System.out.println("\nIV Options:");
        System.out.println("1. Generate randomly");
        System.out.println("2. Enter manually (32 hex characters)");
        System.out.print("Enter choice: ");
        int choice = readInt();
        
        if (choice == 1) {
            currentIV = KeyExpansion.generateRandom(16);
            System.out.println("Generated IV (Hex): " + bytesToHex(currentIV));
            
        } else if (choice == 2) {
            System.out.print("Enter 32 hex characters: ");
            String hex = sc.nextLine().replaceAll("\\s+", "");
            
            if (isValidHex(hex) && hex.length() == 32) {
                currentIV = hexToBytes(hex);
                System.out.println("IV set successfully.");
                
            } else {
                System.out.println("Invalid! Must be exactly 32 hex characters.");
            }
            
        } else {
            System.out.println("Invalid choice.");
        }
    }
 
    private static void keyExpansionMenu() {
        if (currentKey == null) {
            System.out.println("Please set a key first!");
            return;
        }
        
        if (currentKey.length != keySize / 8) {
            System.out.println("Error: Key size conflict. Please regenerate key.");
            return;
        }
        
        System.out.println("\nKey Expansion:");
        System.out.println("1. Show AES key scheduling process");
        System.out.println("2. Display all round keys");
        System.out.println("3. Display round keys in binary and hex");
        System.out.print("Enter choice: ");
        int choice = readInt();
        
        if (choice == 1 || choice == 2 || choice == 3) {
            KeyExpansion.visualizeKeyExpansion(currentKey);
            
        } else {
            System.out.println("Invalid choice.");
        }
    }
 
    private static void encryptMenu() {
        if (currentKey == null) {
            System.out.println("Error: Set a valid key first.");
            return;
        }
        
        if (mode.equals("CFB") && currentIV == null) {
            System.out.println("Error: CFB requires a 16-byte IV.");
            return;
        }
        
        if (mode.equals("OFB") && currentNonce == null) {
            System.out.println("Error: OFB requires a 16-byte Nonce.");
            return;
        }
 
        System.out.println("\nEncryption:");
        System.out.println("1. Enter Plaintext (Console)");
        System.out.println("2. Enter Hexadecimal Plaintext (Console)");
        System.out.println("3. Read from File (.txt / .docx / .jpg)");
        System.out.print("Enter choice: ");
        int choice = readInt();
 
        try {
        	
            byte[] data = null;
            String sourceFile = "";
 
            if (choice == 1) {
                System.out.print("Enter plaintext: ");
                data = sc.nextLine().getBytes();
                
            } else if (choice == 2) {
                System.out.print("Enter hex plaintext: ");
                String hex = sc.nextLine().replaceAll("\\s+", "");
                
                if (!isValidHex(hex)) {
                    System.out.println("Invalid Hex Input!");
                    return;
                }
                
                data = hexToBytes(hex);
                
            } else if (choice == 3) {
                System.out.print("Enter file path: ");
                sourceFile = sc.nextLine().trim();
                String ext = sourceFile.substring(sourceFile.lastIndexOf("."));
                
                if (!ext.equals(".txt") && !ext.equals(".docx") && !ext.equals(".jpg")) {
                    System.out.println("Unsupported file type! Use .txt .docx .jpg");
                    return;
                }
                
                data = FileManager.readFileBinary2(sourceFile);
                
            } else {
                System.out.println("Invalid choice.");
                return;
            }
 
            byte[] ivOrNonce = null;
            if (mode.equals("CFB")) {
                ivOrNonce = currentIV;
            } else if (mode.equals("OFB")) {
                ivOrNonce = currentNonce;
            }
 
            byte[] cipherBytes = AES.encryptData(data, currentKey, ivOrNonce, mode);
            lastCiphertextHex = bytesToHex(cipherBytes);
 
            System.out.println("\nEncryption Done!");
            System.out.println("Ciphertext (Hex): " + lastCiphertextHex);
 
            System.out.println("\nOutput Options:");
            System.out.println("1. Display on console only");
            System.out.println("2. Save hex to text file");
            System.out.println("3. Save binary to .aes file");
            System.out.print("Enter choice: ");
            int outChoice = readInt();
 
            if (outChoice == 2) {
                System.out.print("Enter output filename: ");
                String outName = sc.nextLine().trim();
                FileManager.writeToFile(lastCiphertextHex, outName);
                System.out.println("Saved: " + outName);
                
            } else if (outChoice == 3) {
                String aesOut = "";
                
                if (sourceFile.isEmpty()) {
                    aesOut = "output.aes";
                    
                } else {
                    aesOut = sourceFile + ".aes";
                }
                
                FileManager.writeBinaryFile2(cipherBytes, aesOut);
                lastAesFile = aesOut;
                System.out.println("Saved: " + aesOut);
            }
 
        } catch (Exception e) {
            System.out.println("Encryption failed: " + e.getMessage());
        }
    }
 
    private static void decryptMenu() {
        if (currentKey == null) {
            System.out.println("Error: Set a valid key first.");
            return;
        }
 
        System.out.println("\nDecryption:");
        System.out.println("1. Enter Ciphertext Hex (Console)");
        System.out.println("2. Read Hex from Text File");
        System.out.println("3. Read Binary from .aes File");
        System.out.print("Enter choice: ");
        int choice = readInt();
 
        try {
        	
            byte[] cipherBytes = null;
 
            if (choice == 1) {
                System.out.print("Enter ciphertext hex: ");
                String hex = sc.nextLine().replaceAll("\\s+", "");
                
                if (!isValidHex(hex)) {
                    System.out.println("Invalid Hex!");
                    return;
                }
                
                cipherBytes = hexToBytes(hex);
                
            } else if (choice == 2) {
                System.out.print("Enter text file path: ");
                String path = sc.nextLine().trim();
                String fileHex = FileManager.readData(path);
                
                if (fileHex == null) {
                    System.out.println("Error reading file!");
                    return;
                }
                
                cipherBytes = hexToBytes(fileHex.replaceAll("\\s+", ""));
                
            } else if (choice == 3) {
                System.out.print("Enter .aes file path: ");
                String path = sc.nextLine().trim();
                
                if (!path.endsWith(".aes")) {
                    System.out.println("File must have .aes extension!");
                    return;
                }
                
                cipherBytes = FileManager.readFileBinary2(path);
                
            } else {
                System.out.println("Invalid choice.");
                return;
            }
 
            byte[] ivOrNonce = null;
            if (mode.equals("CFB")) {
                ivOrNonce = currentIV;
                
            } else if (mode.equals("OFB")) {
                ivOrNonce = currentNonce;
            }
 
            byte[] plainBytes = AES.decryptData(cipherBytes, currentKey, ivOrNonce, mode);
 
            System.out.println("\nDecryption Done!");
            System.out.println("Plaintext (Hex): " + bytesToHex(plainBytes));
            System.out.println("Plaintext (Text): " + bytesToDisplayString(plainBytes));
 
            System.out.println("\n1. Display only");
            System.out.println("2. Save to file");
            System.out.print("Choice: ");
            int saveChoice = readInt();
            
            if (saveChoice == 2) {
                System.out.print("Enter output filename: ");
                String outName = sc.nextLine().trim();
                FileManager.writeBinaryFile2(plainBytes, outName);
                System.out.println("Saved: " + outName);
            }
            
 
        } catch (Exception e) {
            System.out.println("Decryption failed: " + e.getMessage());
        }
    }
 
    private static void sendCiphertextEmail() {
        System.out.print("Enter recipient email: ");
        String to = sc.nextLine().trim();
 
        System.out.println("1. Send last ciphertext as text");
        System.out.println("2. Send .aes file as attachment");
        System.out.print("Enter choice: ");
        int choice = readInt();
 
        if (choice == 1) {
        	
            if (lastCiphertextHex.isEmpty()) {
                System.out.print("No ciphertext yet. Enter hex manually: ");
                lastCiphertextHex = sc.nextLine().replaceAll("\\s+", "");
            }
            
            String content = "AES Ciphertext (Hex):\n" + lastCiphertextHex + "\nMode: " + mode + "\nKey Size: " + keySize;
            Email.send(to, "AES Ciphertext Output", content);
            System.out.println("Email sent!");
            
        } else if (choice == 2) {
            if (lastAesFile.isEmpty() || !new File(lastAesFile).exists()) {
                System.out.print("Enter .aes file path: ");
                lastAesFile = sc.nextLine().trim();
            }
            
            Email.sendWithAttachment(to, "AES Encrypted File", "Please find the attached .aes file.", lastAesFile);
            System.out.println("Email sent!");
            
        } else {
            System.out.println("Invalid choice.");
        }
    }
 
    private static void sendKeyMaterialEmail() {
        if (currentKey == null) {
            System.out.println("No key available to send.");
            return;
        }
 
        System.out.print("Enter recipient email: ");
        String to = sc.nextLine().trim();
 
        String body = "AES Key Data:\n"
                + "Key Size: " + keySize + " bits\n"
                + "Mode: " + mode + "\n"
                + "Key (Hex): " + bytesToHex(currentKey) + "\n";
 
        if (mode.equals("CFB") && currentIV != null) {
            body += "IV (Hex): " + bytesToHex(currentIV) + "\n";
        }
        
        if (mode.equals("OFB") && currentNonce != null) {
            body += "Nonce (Hex): " + bytesToHex(currentNonce) + "\n";
        }
 
        Email.send(to, "AES Key Material", body);
        System.out.println("Email sent!");
    }
 
    private static String bytesToDisplayString(byte[] data) {
        String result = "";
        for (int i = 0; i < data.length; i++) {
            int b = data[i];
            if (b >= 32 && b <= 126) {
                result += (char) b;
                
            } else {
                result += '.';
            }
        }
        return result;
    }
 
    private static String bytesToHex(byte[] bytes) {
        String result = "";
        char[] hexa = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i];
            
            if (v < 0) {
                v += 256;
            }
            
            result += hexa[v / 16];
            result += hexa[v % 16];
        }
        return result;
    }
 
    private static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int v1 = 0;
            int v2 = 0;
            char c1 = s.charAt(i);
            char c2 = s.charAt(i + 1);
            if (c1 >= '0' && c1 <= '9') {
                v1 = c1 - '0';
                
            } else if (c1 >= 'A' && c1 <= 'F') {
                v1 = c1 - 'A' + 10;
                
            } else if (c1 >= 'a' && c1 <= 'f') {
                v1 = c1 - 'a' + 10;
            }
            if (c2 >= '0' && c2 <= '9') {
                v2 = c2 - '0';
                
            } else if (c2 >= 'A' && c2 <= 'F') {
                v2 = c2 - 'A' + 10;
                
            } else if (c2 >= 'a' && c2 <= 'f') {
                v2 = c2 - 'a' + 10;
                
            }
            data[i / 2] = (byte) ((v1 * 16) + v2);
            
        }
        return data;
    }
 
    private static boolean isValidHex(String hex) {
        if (hex == null || hex.length() == 0 || hex.length() % 2 != 0) {
            return false;
        }
        
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            
            if ((c < '0' || c > '9') && (c < 'A' || c > 'F') && (c < 'a' || c > 'f')) {
                return false;
            }
            
        }
        
        return true;
    }
}