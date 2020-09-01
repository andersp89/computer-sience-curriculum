package week2;

import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

/**
 * A randomized queue is similar to a stack or queue, 
 * except that the item removed is chosen uniformly at 
 * random among items in the data structure. 
 * @author anderspedersen
 *
 * @param <Item>
 */

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] a; //array of items
    private int n; //element of queue, index of first and last.

    // construct an empty randomized queue
    public RandomizedQueue() {
	a = (Item[]) new Object[2]; //the ugly cast as arrays do not support generics
	n = 0;
    }

    // is the randomized queue empty?
    public boolean isEmpty() { return n == 0; }

    // return the number of items on the randomized queue
    public int size() { return n; }

    // resize array, double when full
    private void resize(int capacity) {
	//initialize empty array of capacity length and copy value of a
	Item[] temp = (Item[]) new Object[capacity]; //the ugly cast
	for (int i = 0; i < n; i++) {
	    temp[i] = a[i];
	}
	// point a of object to temp, start from 0, and last by size of array
	a = temp;
    }

    // add the item 
    public void enqueue(Item item) {
	if (item == null) throw new IllegalArgumentException();
	if (n == a.length) resize(2 * a.length); //double array size when length is reached
	a[n++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
	if (isEmpty()) throw new NoSuchElementException("Queue underflow");
	int rIndex = StdRandom.uniform(n);
	//copy the position to return
	Item item = a[rIndex];
	//assign the random position to the last element (last element is at n-1)
	a[rIndex] = a[--n]; //--n decrements n and returns that value
	// null last position 
	a[n] = null; //avoid loitering (holding a reference to an object no longer needed)
	//shrink array if necessary, half its size if using 25% of length
	if (n > 0 && n == a.length/4) resize(a.length/2);
	return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
	if (isEmpty()) throw new NoSuchElementException("Queue underflow");
	//rIndex = StdRandom.uniform(n+1);
	return a[StdRandom.uniform(n)];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
	return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<Item> {
	private int i = 0;
	private int[] shuffledIndexes = new int[n]; 

	public boolean hasNext() { 
	    if (i == 0) shuffleIndexes();
	    return i < n; 
	}
	public void remove() { throw new UnsupportedOperationException(); }
	public Item next() {
	    if (i >= n || size() == 0) throw new NoSuchElementException();
	    Item item = a[shuffledIndexes[i++]];
	    return item;
	}
	
	private void shuffleIndexes() {
	    for (int i = 0; i < n; i++) {
		shuffledIndexes[i] = i;
	    }
	    StdRandom.shuffle(shuffledIndexes);
	}
	
    }

    // unit testing (required)
    public static void main(String[] args) {
	RandomizedQueue<Integer> rq = new RandomizedQueue<>(); 

	//test isEmpty & Size
	StdOut.println("Empty?: " + rq.isEmpty());
	StdOut.println("Size: " + rq.size());

	//create integers
	Integer integer = new Integer(1);
	rq.enqueue(integer);
	Integer integer2 = new Integer(2);
	rq.enqueue(integer2);
	Integer integer3 = new Integer(3);
	rq.enqueue(integer3);
	Integer integer4 = new Integer(4);
	rq.enqueue(integer4);

	//print
	for (Integer i : rq) {
	    StdOut.println("Tal: " + i);
	}
	StdOut.println("Size: " + rq.size());

	//sample test
	StdOut.println("Pick some random: " + rq.sample());
	StdOut.println("Pick some random: " + rq.sample());

	//dequeue test, resizing array
	rq.dequeue();
	rq.dequeue();
	rq.dequeue();

	//print
	for (Integer i : rq) {
	    StdOut.println("Tal: " + i);
	}
	StdOut.println("Size: " + rq.size());
    }

}