import java.util.Scanner;

public class HammingCode {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int i, j, k, count, err_pos = 0, flag = 0;

        char[] data = new char[7];      // 7-bit data
        char[] dw = new char[12];       // 11-bit codeword (index 0 to 11)
        char[] cw = new char[12];       // received codeword

        System.out.println("Enter data as binary bit stream (7 bits):");
        String input = sc.nextLine();
        input.getChars(0, 7, data, 0);

        // Step 1: Place data and parity bits in codeword
        for (i = 0, j = 0, k = 0; i < 11; i++) {
            if (i == (int) Math.pow(2, j) - 1) {
                dw[i] = '0'; // Placeholder for parity
                j++;
            } else {
                dw[i] = data[k++];
            }
        }

        // Step 2: Calculate parity bits
        for (i = 0; i < 4; i++) {
            count = 0;
            int parityPos = (int) Math.pow(2, i);
            for (j = parityPos - 1; j < 11; j += 2 * parityPos) {
                for (k = 0; k < parityPos && j + k < 11; k++) {
                    if (dw[j + k] == '1') {
                        count++;
                    }
                }
            }
            dw[parityPos - 1] = (count % 2 == 0) ? '0' : '1';
        }

        // Step 3: Print generated codeword
        System.out.println("Generated code word is:");
        for (i = 0; i < 11; i++) {
            System.out.print(dw[i]);
        }

        // Step 4: Input received codeword
        System.out.println("\n\nEnter the received Hamming code (11 bits):");
        String received = sc.nextLine();
        received.getChars(0, 11, cw, 0);

        // Step 5: Error detection
        err_pos = 0;
        for (i = 0; i < 4; i++) {
            count = 0;
            int parityPos = (int) Math.pow(2, i);
            for (j = parityPos - 1; j < 11; j += 2 * parityPos) {
                for (k = 0; k < parityPos && j + k < 11; k++) {
                    if (cw[j + k] == '1') {
                        count++;
                    }
                }
            }
            if (count % 2 != 0) {
                err_pos += parityPos;
            }
        }

        // Step 6: Error correction
        if (err_pos == 0) {
            System.out.println("\nThere is no error in the received code word.");
        } else {
            int pos = err_pos - 1;
            if (cw[pos] == dw[pos]) {
                System.out.println("\nThere are 2 or more errors in the received code...");
                System.out.println("Sorry...! Hamming code cannot correct 2 or more errors.");
                flag = 1;
            } else {
                System.out.println("\nThere is an error in bit position " + err_pos + " of the received code word.");
                if (flag == 0) {
                    // Correct the bit
                    cw[pos] = (cw[pos] == '1') ? '0' : '1';
                    System.out.println("\nCorrected code word is:");
                    for (i = 0; i < 11; i++) {
                        System.out.print(cw[i]);
                    }
                }
            }
        }

        System.out.println("\n");
        sc.close();
    }
}
