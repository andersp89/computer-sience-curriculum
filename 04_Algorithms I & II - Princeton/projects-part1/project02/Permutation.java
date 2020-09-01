package week2;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * Write a client program Permutation.java that:
 * _ takes an integer k as a !!command-line!! argument;
 * _ reads a sequence of strings from standard input using StdIn.readString(); 
 * _ and prints exactly k of them, uniformly at random. Print each item from the sequence at most once.
 * @author anderspedersen
 *
 */

public class Permutation {
    /*
     * 0 <= k <= n, where n is the number of string on standard input.
     * The running time must be linear in the size of the input.
     * You may use only a constant amount of memory plus either one Deque or RandomizedQueue object
     * of maximum size at most n. 
     */
    public static void main (String[] args) {
	//k strings to print
	int k = Integer.parseInt(args[0]);

	//read strings on std input - hit ctrl+d, click off console and back to send EOF
	RandomizedQueue<String> rq = new RandomizedQueue<>();
	while (!StdIn.isEmpty()) {
	    rq.enqueue(StdIn.readString());
	}

	//print k-number strings uniformly random
	for (int i = 0; i < k; i++) {
	    StdOut.println(rq.dequeue());
	}
    }

}
