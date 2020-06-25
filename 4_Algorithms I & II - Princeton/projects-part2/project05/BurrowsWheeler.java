package week5;
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int R = 256; //size of alphabet
    
    /**
     * The Burrows–Wheeler transform is the last column in 
     * the sorted suffixes array t[], preceded by the row 
     * number first in which the original string ends up. 
     * 
     * e.g. 
     * 3
     * ARD!RCAAAABB
     * 
     * reading from standard input and writing to standard output
     */
    public static void transform() {
	//read
	String input = BinaryStdIn.readString();
	
	//build last col and row number in which original string shows up
	CircularSuffixArray csa = new CircularSuffixArray(input);
	int first = 0;
	int len = csa.length();
	StringBuilder lastCol = new StringBuilder();
	
	for (int i = 0; i < len; i++) {
	    //if original string
	    if (csa.index(i) == 0) {
		first = i;
		lastCol.append(input.charAt(len - 1)); //position is the last char in original word
	    } else {
		lastCol.append(input.charAt(csa.index(i) - 1)); //append the position in original word
	    }
	}
	
	//write to std out
	BinaryStdOut.write(first);
	BinaryStdOut.write(lastCol.toString());
	BinaryStdOut.close();
    }

    /**
     * apply Burrows-Wheeler inverse transform,
     * reading from standard input and writing to standard output
     */
    public static void inverseTransform() {
	//read
	int first = BinaryStdIn.readInt();
	String input = BinaryStdIn.readString();
	int len = input.length();
	
	//convert to array
	char[] t = new char[len];
	for (int i = 0; i < len; i++) 
	    t[i] = input.charAt(i);
	
	
	//perform key indexed counting, modify it to create both the sorted and the next array
	int[] next = new int[len];
	char[] sorted = new char[len];
	int[] count = new int[R+1];

	for (int i = 0; i < len; i++) 
	    count[t[i] + 1]++;
	
	for (int r = 0; r < R; r++) 
	    count[r + 1] += count[r];
	
	for (int i = 0; i < len; i++) {
	    int index = count[t[i]]++;
	    sorted[index] = t[i];
	    next[index] = i;
	}
	
	//trace the characters starting at first and using the next array
	int a = first;
	int counter = 0;
	while (counter < len) {
	    BinaryStdOut.write(sorted[a]);
	    a = next[a];
	    counter++;
	}
	BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
	if (args[0].equals("-")) transform();
	else if (args[0].equals("+")) inverseTransform();
    }

}