package week2;

import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.StdOut;

/**
 * Deque: A double-ended queue or deque (pronounced “deck”) is a generalization 
 * of a stack and a queue that supports adding and removing items from either the 
 * front or the back of the data structure. 
 * @author anderspedersen
 *
 * @param <Item>
 */

public class Deque<Item> implements Iterable<Item> {
    private int n; //size of deque
    private Node first, last; //top and bottom of stack

    // helper linked list class
    private class Node {
	private Item item;
	private Node next;
	private Node previous;
    }

    // construct an empty deque
    public Deque() {
	first  = null;
	last = null;
	n = 0;
    }

    // is the deque empty?
    public boolean isEmpty() { return first == null; }

    // return the number of items on the deque
    public int size() {	return n; }

    // add the item to the front
    public void addFirst(Item item) {
	if (item == null) throw new IllegalArgumentException();
	//new node for new first
	Node newFirst = new Node();
	newFirst.item = item;
	if (!isEmpty()) {
	    //set next & previous
	    newFirst.next = first;
	    first.previous = newFirst;
	    //set new first as the first
	    first = newFirst;
	} else {
	    first = newFirst;
	    last = newFirst;
	}
	n++;
    }

    // add the item to the back
    public void addLast(Item item) {
	if (item == null) throw new IllegalArgumentException();
	Node newLast = new Node();
	newLast.item = item;
	if (!isEmpty()) {
	    last.next = newLast;
	    newLast.previous = last;
	    last = newLast;
	} else {
	    first = newLast;
	    last = newLast;
	}
	n++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
	if (isEmpty()) throw new NoSuchElementException();
	Item item = first.item;
	first = first.next;
	n--;	
	return item;
    }

    // remove and return the item from the back
    //do we need to check whether a first exist???
    public Item removeLast() {
	if (isEmpty()) throw new NoSuchElementException();
	//save the current last item
	Item item = last.item;
	//set the second last as new last
	last = last.previous;
	//return the old last
	n--;
	return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
	return new ListIterator();
    }

    private class ListIterator implements Iterator<Item> {
	private Node current = first;
	public boolean hasNext() { return current != null; }
	public void remove() { throw new UnsupportedOperationException(); }
	public Item next() {
	    if (current == null) { throw new NoSuchElementException(); }
	    Item item = current.item;
	    current = current.next;
	    return item;
	}
    }



    // unit testing (required)
    public static void main(String[] args) {
	//initiate new deque
	Deque<String> deck = new Deque<>();
	
	//test addFirst
	String item = "new string";
	deck.addFirst(item);
	//test isEmpty & size
	StdOut.println(deck.isEmpty());
	StdOut.println(deck.size());
	//test addLast
	String item2 = "new string2";
	deck.addLast(item2);
	//test addFirst
	String item3 = "new string3";
	deck.addFirst(item3);
	// print input
	for (String string : deck) {
	    StdOut.println(string + " ");
	}
	StdOut.println();
	//test removeFirst & removeLast
	StdOut.println("Remove first: " + deck.removeFirst());
	StdOut.println("Remove last: " + deck.removeLast());
	StdOut.println();
	//print	
	for (String string : deck) {
	    StdOut.println(string + " ");
	}
	StdOut.println();
	//test isEmpty & size
	StdOut.println(deck.isEmpty());
	StdOut.println(deck.size());
    }

}