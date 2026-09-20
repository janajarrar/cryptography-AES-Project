package PROJECT;

public class AES {

    static char[] hexa = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public static byte[] encryptData(byte[] data, byte[] key, byte[] iv, String mode) {
        data = addPadding(data);
        int[][] w = KeyExpansion.RoundKeys(key);

        byte[] output = new byte[data.length];

        byte[] currentIV = new byte[16];
        if (iv != null) {
            for (int i = 0; i < 16; i++)
                currentIV[i] = iv[i];
        }

        int totalBlocks = data.length / 16;

        for (int i = 0; i < totalBlocks; i++) {
            byte[] block = new byte[16];
            
            for (int j = 0; j < 16; j++)
                block[j] = data[i * 16 + j];
            
            byte[] encryptedBlock = new byte[16];

            switch (mode) {
                case "ECB":
                    encryptedBlock = encryptBlock(block, w);
                    break;
                    
                case "OFB":
                    byte[] keystream = encryptBlock(currentIV, w);
                    for (int k = 0; k < 16; k++)
                        encryptedBlock[k] = (byte)(block[k] ^ keystream[k]);
                    for (int k = 0; k < 16; k++)
                        currentIV[k] = keystream[k];
                    break;
                    
                case "CFB":
                    byte[] encryptedIV = encryptBlock(currentIV, w);
                    xorBlocks(block, encryptedIV);
                    encryptedBlock = block;
                    for (int k = 0; k < 16; k++)
                        currentIV[k] = encryptedBlock[k];
                    break;
                    
                default:
                    System.out.println("invalid!");
                    break;
            }

            for (int j = 0; j < 16; j++)
                output[i * 16 + j] = encryptedBlock[j];
        }
        return output;
    }

    public static byte[] decryptData(byte[] data, byte[] key, byte[] iv, String mode) {
        int[][] w = KeyExpansion.RoundKeys(key);
        byte[] output = new byte[data.length];

        byte[] currentIV = new byte[16];
        if (iv != null) {
            for (int i = 0; i < 16; i++)
                currentIV[i] = iv[i];
        }

        int totalBlocks = data.length / 16;

        for (int i = 0; i < totalBlocks; i++) {
            byte[] block = new byte[16];
            for (int j = 0; j < 16; j++)
                block[j] = data[i * 16 + j];

            byte[] decryptedBlock = new byte[16];

            switch (mode) {
                case "ECB":
                    decryptedBlock = decryptBlock(block, w);
                    break;
                    
                case "OFB":
                    byte[] keystream = encryptBlock(currentIV, w);
                    for (int k = 0; k < 16; k++)
                        decryptedBlock[k] = (byte)(block[k] ^ keystream[k]);
                    for (int k = 0; k < 16; k++)
                        currentIV[k] = keystream[k];
                    break;
                    
                case "CFB":
                    byte[] encryptedIV = encryptBlock(currentIV, w);
                    
                    xorBlocks(block, encryptedIV);
                    decryptedBlock = block;
                    
                    for (int k = 0; k < 16; k++)
                        currentIV[k] = data[i * 16 + k];
                    break;
                    
                default:
                    System.out.println("Invalid!");
                    break;
            }

            for (int j = 0; j < 16; j++)
                output[i * 16 + j] = decryptedBlock[j];
        }
        return removePadding(output);
    }

    public static void visualizeEncryption(byte[] data, byte[] key) {
        System.out.println(" Encryption Steps:");
        System.out.println("-------------------");

        byte[] block = new byte[16];
        for (int i = 0; i < 16; i++)
            block[i] = data[i];

        int[][] w = KeyExpansion.RoundKeys(key);
        int rounds = (w.length / 4) - 1;

        int[][] s = new int[4][4];
        for (int i = 0; i < 16; i++) {
            int val = block[i];
            
            if (val < 0) 
            	val += 256;
            
            s[i % 4][i / 4] = val;
        }

        System.out.println("Initial State:");
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int v = s[r][c];
                
                System.out.print(hexa[v / 16] + "" + hexa[v % 16] + " ");
            }
            System.out.println();
        }

        addRoundKey(s, w, 0);
        System.out.println("\n[Round 0] AddRoundKey ");

        for (int r = 1; r < rounds; r++) {
            System.out.println("\n---Round " + r + "---");
            subBytes(s);
            shiftRows(s);
            mixColumns(s);
            addRoundKey(s, w, r);

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    int v = s[i][j];
                    System.out.print(hexa[v / 16] + "" + hexa[v % 16] + " ");
                }
                System.out.println();
            }
        }

        System.out.println("\nFinal Round: ");
        subBytes(s);
        shiftRows(s);
        addRoundKey(s, w, rounds);

        System.out.println("Final Output:");
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int v = s[r][c];
                System.out.print(hexa[v / 16] + "" + hexa[v % 16] + " ");
            }
            System.out.println();
        }
        System.out.println("----------------------");
    }

    public static void visualizeDecryption(byte[] data, byte[] key) {
        System.out.println(" Decryption Steps: ");
        System.out.println("--------------------");

        byte[] block = new byte[16];
        for (int i = 0; i < 16; i++)
            block[i] = data[i];

        int[][] w = KeyExpansion.RoundKeys(key);
        int rounds = (w.length / 4) - 1;

        int[][] s = new int[4][4];
        
        for (int i = 0; i < 16; i++) {
            int val = block[i];
            
            if (val < 0) 
            	val += 256;
            
            s[i % 4][i / 4] = val;
        }

        System.out.println("Cipher Input:");
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int v = s[r][c];
                System.out.print(hexa[v / 16] + "" + hexa[v % 16] + " ");
            }
            System.out.println();
        }

        System.out.println("\n[Round 0] Initial Key Added");
        addRoundKey(s, w, rounds);

        for (int r = rounds - 1; r > 0; r--) {
            System.out.println("\n---Round " + (rounds - r) + "---");
            
            invShiftRows(s);
            invSubBytes(s);
            addRoundKey(s, w, r);
            invMixColumns(s);

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    int v = s[i][j];
                    System.out.print(hexa[v / 16] + "" + hexa[v % 16] + " ");
                }
                System.out.println();
            }
        }

        System.out.println("\n--- Final Round ---");
        invShiftRows(s);
        invSubBytes(s);
        addRoundKey(s, w, 0);

        System.out.println("Plaintext Output:");
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int v = s[r][c];
                System.out.print(hexa[v / 16] + "" + hexa[v % 16] + " ");
            }
            System.out.println();
        }
        System.out.println("-------------------");
    }

    private static byte[] encryptBlock(byte[] in, int[][] w) {
        int Nr = (w.length / 4) - 1;
        int[][] s = new int[4][4];

        for (int i = 0; i < 16; i++) {
            int val = in[i];
            
            if (val < 0) 
            	val += 256;
            
            s[i % 4][i / 4] = val;
        }

        addRoundKey(s, w, 0);

        for(int r = 1; r < Nr; r++) {
            subBytes(s);
            shiftRows(s);
            mixColumns(s);
            addRoundKey(s, w, r);
        }

        subBytes(s);
        shiftRows(s);
        addRoundKey(s, w, Nr);

        byte[] out = new byte[16];
        
        for (int i = 0; i < 16; i++)
            out[i] = (byte)s[i % 4][i / 4];

        return out;
    }

    private static byte[] decryptBlock(byte[] in, int[][] w) {
        int Nr = (w.length / 4) - 1;
        int[][] s = new int[4][4];

        for (int i = 0; i < 16; i++) {
            int val = in[i];
            
            if (val < 0) 
            	val += 256;
            
            s[i % 4][i / 4] = val;
        }

        addRoundKey(s, w, Nr);

        for (int r = Nr - 1; r > 0; r--) {
            invShiftRows(s);
            invSubBytes(s);
            addRoundKey(s, w, r);
            invMixColumns(s);
        }

        invShiftRows(s);
        invSubBytes(s);
        addRoundKey(s, w, 0);

        byte[] out = new byte[16];
        
        for (int i = 0; i < 16; i++)
            out[i] = (byte)s[i % 4][i / 4];

        return out;
    }

    private static void subBytes(int[][] s) {
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                s[i][j] = Tables.sBox[s[i][j]];
    }

    private static void invSubBytes(int[][] s) {
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                s[i][j] = Tables.invSBox[s[i][j]];
    }

    private static void addRoundKey(int[][] s, int[][] w, int r) {
        for (int c = 0; c < 4; c++)
            for (int row = 0; row < 4; row++)
                s[row][c] = s[row][c] ^ w[r * 4 + c][row];
    }

    private static void shiftRows(int[][] s) {
        int temp = s[1][0];
        s[1][0] = s[1][1];
        s[1][1] = s[1][2];
        s[1][2] = s[1][3];
        s[1][3] = temp;

        temp = s[2][0];
        int temp2 = s[2][1];
        s[2][0] = s[2][2];
        s[2][1] = s[2][3];
        s[2][2] = temp;
        s[2][3] = temp2;

        temp = s[3][3];
        s[3][3] = s[3][2];
        s[3][2] = s[3][1];
        s[3][1] = s[3][0];
        s[3][0] = temp;
    }

    private static void invShiftRows(int[][] s) {
        int temp = s[1][3];
        s[1][3] = s[1][2];
        s[1][2] = s[1][1];
        s[1][1] = s[1][0];
        s[1][0] = temp;

        temp = s[2][0];
        int temp2 = s[2][1];
        s[2][0] = s[2][2];
        s[2][1] = s[2][3];
        s[2][2] = temp;
        s[2][3] = temp2;

        temp = s[3][0];
        s[3][0] = s[3][1];
        s[3][1] = s[3][2];
        s[3][2] = s[3][3];
        s[3][3] = temp;
    }

    private static void mixColumns(int[][] s) {
        int[] t = new int[4];
        
        for (int c = 0; c < 4; c++) {
            for (int i = 0; i < 4; i++)
                t[i] = s[i][c];
            
            s[0][c] = gFun(2, t[0]) ^ gFun(3, t[1]) ^ t[2] ^ t[3];
            s[1][c] = t[0] ^ gFun(2, t[1]) ^ gFun(3, t[2]) ^ t[3];
            s[2][c] = t[0] ^ t[1] ^ gFun(2, t[2]) ^ gFun(3, t[3]);
            s[3][c] = gFun(3, t[0]) ^ t[1] ^ t[2] ^ gFun(2, t[3]);
        }
    }

    private static void invMixColumns(int[][] s) {
        int[] t = new int[4];
        
        for (int c = 0; c < 4; c++) {
            for (int i = 0; i < 4; i++)
                t[i] = s[i][c];
            s[0][c] = gFun(14, t[0]) ^ gFun(11, t[1]) ^ gFun(13, t[2]) ^ gFun(9, t[3]);
            s[1][c] = gFun(9, t[0]) ^ gFun(14, t[1]) ^ gFun(11, t[2]) ^ gFun(13, t[3]);
            s[2][c] = gFun(13, t[0]) ^ gFun(9, t[1]) ^ gFun(14, t[2]) ^ gFun(11, t[3]);
            s[3][c] = gFun(11, t[0]) ^ gFun(13, t[1]) ^ gFun(9, t[2]) ^ gFun(14, t[3]);
        }
    }

    private static int gFun(int a, int b) {
        int result = 0;

        for (int i = 0; i < 8; i++) { 
            int lastBit = b % 2;
            switch (lastBit) {
                case 1:
                    result = result ^ a;
                    break;
            }
            int highBit = a / 128;
            a = a * 2;
            
            switch (highBit) {
                case 1:
                    a = a ^ 27;
                    break;
            }
            b = b / 2;
            a = a % 256;
        }
        
        return result;
    }

    private static void xorBlocks(byte[] a, byte[] b) {
        for (int i = 0; i < 16; i++)
            a[i] = (byte) (a[i] ^ b[i]);
    }

    public static byte[] addPadding(byte[] data) {
        int remainder = data.length % 16;
        int paddingNeeded = 16 - remainder;
        
        byte[] newData = new byte[data.length + paddingNeeded];
        
        for (int i = 0; i < data.length; i++)
            newData[i] = data[i];
        
        for (int i = data.length; i < newData.length; i++)
            newData[i] = (byte) paddingNeeded;
        
        return newData;
    }

    public static byte[] removePadding(byte[] data) {
        int len = data.length;
        int paddingVal = data[len - 1];
        
        if (paddingVal < 0) 
        	paddingVal += 256;
        
        int newLen = len - paddingVal;
        byte[] finalData = new byte[newLen];
        
        for (int i = 0; i < newLen; i++)
            finalData[i] = data[i];
        return finalData;
    }

    public static String bytesToHex(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i];
            
            if (v < 0) 
            	v += 256;
            
            result += hexa[v / 16];
            result += hexa[v % 16];
        }
        return result;
    }

    public static boolean isValidHex(String s) {
        if (s == null || s.length() == 0 || s.length() % 2 != 0)
            return false;
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            
            if ((c < '0' || c > '9') && (c < 'A' || c > 'F') && (c < 'a' || c > 'f'))
                return false;
        }
        return true;
    }

    public static boolean validateKeyLength(byte[] key, int keySizeBits) {
        return key != null && key.length == keySizeBits / 8;
    }

    public static boolean validateIV(byte[] iv) {
        return iv != null && iv.length == 16;
    }

    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        
        for (int i = 0; i < len; i += 2) {
            char c1 = s.charAt(i);
            char c2 = s.charAt(i + 1);
            int v1 = 0;
            int v2 = 0;
            
            if (c1 >= '0' && c1 <= '9') 
            	v1 = c1 - '0';
            
            else if (c1 >= 'A' && c1 <= 'F') 
            	v1 = c1 - 'A' + 10;
            
            else if (c1 >= 'a' && c1 <= 'f') 
            	v1 = c1 - 'a' + 10;
            
            if (c2 >= '0' && c2 <= '9') 
            	v2 = c2 - '0';
            
            else if (c2 >= 'A' && c2 <= 'F') 
            	v2 = c2 - 'A' + 10;
            
            else if (c2 >= 'a' && c2 <= 'f') 
            	v2 = c2 - 'a' + 10;
            
            data[i / 2] = (byte) ((v1 * 16) + v2);
        }
        return data;
    }
}