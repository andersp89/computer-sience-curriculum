package week5;
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * Maintain an ordered sequence of the characters in the 
 * alphabet, and repeatedly read in a character from the input message, print
 * out the position in which that character appears, and move that character
 * to the front of the sequence.
 * @author anderspedersen
 *
 */
public class MoveToFront {
    private static final int R = 256; // size of alphabet
    
    /**
     *  apply move-to-front encoding, reading from standard input and writing to standard output
     */
    public static void encode() {
	// create initial ordered sequence of extended ASCII characters
	char[] list = new char[R];
	for (int i = 0; i < R; i++) 
	    list[i] = (char) i;
	
	// for each character input, find the index where it appears
	// and move it to the front of the sequence of characters. Print index.
	while (!BinaryStdIn.isEmpty()) {
	    int counter = 1;
	    int index = 0;
	    char input = BinaryStdIn.readChar();
	    if (list[0] == input) { 
		BinaryStdOut.write(0, 8);
	    } else {
		while (list[index] != input) 
		    index++;
		
		char first = list[index];
		
		for (int i = index; i > 0; i--) 
		    list[i] = list[i-1];
		
		list[0] = first;
		BinaryStdOut.write(index, 8);
	    }
	    
	}
	BinaryStdOut.close();
    }

    /**
     * apply move-to-front decoding, reading from 
     * standard input and writing to standard output
     */
    public static void decode() {
	// create initial ordered sequence of extended ASCII characters
	char[] list = new char[R];
	for (int i = 0; i < list.length; i++) 
	    list[i] = (char) i;
	
	// read in integer i, print the ith character in the sequence
	// then move it to the front of the sequence
	while (!BinaryStdIn.isEmpty()) {
	    int counter = 1;
	    int input = (int) BinaryStdIn.readChar();
	    BinaryStdOut.write(list[input]);
	    
	    if (input != 0) {
		char first = list[input];
		for (int i = input; i > 0; i--) 
		    list[i] = list[i-1];
		
		list[0] = first;
	    }
	}
	BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
	if (args[0].equals("-")) encode();
	else if (args[0].equals("+")) decode();
    }

}