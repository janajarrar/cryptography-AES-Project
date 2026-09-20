package PROJECT;

import java.util.Random;

public class KeyExpansion {
	
	//generate random key
	public static byte[] generateRandom(int size) {
		
	    byte[] key = new byte[size];
	    Random random = new Random();
	    
	    for (int i = 0; i < size; i++) {
	        key[i] = (byte) random.nextInt(256);
	    }
	    return key;
	}

    public static int[][] RoundKeys(byte[] key) {
        int numOfWords = key.length / 4;
        int numOfRounds = numOfWords + 6;
        int blockSize = 4;
        

        int[][] roundKeys = new int[blockSize * (numOfRounds + 1)][4];
        int[] Word = new int[4];

       
        for (int i = 0; i < numOfWords; i++) {
       
            for (int j = 0; j < 4; j++) {
                byte b = key[4 * i + j];
                
                if (b < 0) {
                    roundKeys[i][j] = b + 256;
                } else {
                    roundKeys[i][j] = b;
                }
            }
        }

        for (int i = numOfWords; i < blockSize * (numOfRounds + 1); i++) {
            for (int k = 0; k < 4; k++)
            	Word[k] = roundKeys[i - 1][k];
            
            if (i % numOfWords == 0) {
                int t = Word[0];
                Word[0] = Word[1];
                Word[1] = Word[2];
                Word[2] = Word[3];
                Word[3] = t;

                for (int k = 0; k < 4; k++)
                	Word[k] = Tables.sBox[Word[k]];

                Word[0] ^= Tables.rCon[i / numOfWords];
            } 
            else if (numOfWords > 6 && (i % numOfWords == 4)) {
                for (int k = 0; k < 4; k++)
                	Word[k] = Tables.sBox[Word[k]];
            }

            for (int k = 0; k < 4; k++)
                roundKeys[i][k] = roundKeys[i - numOfWords][k] ^ Word[k];
        }

        return roundKeys;
    }

    public static void visualizeKeyExpansion(byte[] key) {
        System.out.println("\n-----------------------------");
        System.out.println("Key Expansion Process");
        System.out.println("-----------------------------");

        int numOfWords = key.length / 4;
        int numOfRou = numOfWords + 6;
        int blockSize = 4;
       
        char[] hexaChar = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        System.out.println("Words: " + numOfWords);
        System.out.println("Rounds: " + numOfRou);
        
        System.out.print("Original Key: ");
        for (int i = 0; i < key.length; i++) {
            int val = key[i];
            if (val < 0) val = val + 256;
            
            System.out.print("" + hexaChar[val / 16] + hexaChar[val % 16] + " ");
        }
        System.out.println("\n");

        int[][] w = new int[blockSize * (numOfRou + 1)][4];
        int[] temp = new int[4];

        System.out.println("1: Copying Original Key");
        
        for (int i = 0; i < numOfWords; i++) {
            System.out.print("Word " + i + ": ");
            for(int j = 0; j < 4; j++) {
                byte b = key[4 * i + j];
               
                if (b < 0) {
                    w[i][j] = b + 256;
                } else {
                    w[i][j] = b;
                }
               
                int val = w[i][j];
                System.out.print("" + hexaChar[val / 16] + hexaChar[val % 16]);
            }
            System.out.println();
        }

        System.out.println("\n2: Generating New Words");

        for (int i = numOfWords; i < blockSize * (numOfRou + 1); i++) {
            for (int t = 0; t < 4; t++)
                temp[t] = w[i - 1][t];

            if (i % numOfWords == 0) {
                int t = temp[0];
                temp[0] = temp[1];
                temp[1] = temp[2];
                temp[2] = temp[3];
                temp[3] = t;

                for (int k = 0; k < 4; k++)
                    temp[k] = Tables.sBox[temp[k]];

                int rVal = Tables.rCon[i / numOfWords];
                temp[0] = temp[0] ^ rVal;
                
                System.out.println("Step at Word " + i);
            } 
            else if (numOfWords > 6 && (i % numOfWords == 4)) {
                for (int k = 0; k < 4; k++)
                    temp[k] = Tables.sBox[temp[k]];
            }

            System.out.print("Word " + i + ": ");
            for (int k = 0; k < 4; k++) {
                w[i][k] = w[i - numOfWords][k] ^ temp[k];
                
                int val = w[i][k];
                System.out.print("" + hexaChar[val / 16] + hexaChar[val % 16]);
            }
            System.out.println();
        }
        
        System.out.println("\n---------------------------");
        System.out.println("Final Round Keys");
        System.out.println("-----------------------------");
        
        for (int r = 0; r <= numOfRou; r++) {
            System.out.print("Round " + r + ": ");
            for (int c = 0; c < 4; c++) {
                int idx = r * 4 + c;
              
                for(int b = 0; b < 4; b++) {
                    int val = w[idx][b];
                    System.out.print("" + hexaChar[val / 16] + hexaChar[val % 16]);
                }
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println("-----------------------------\n");
    }
}