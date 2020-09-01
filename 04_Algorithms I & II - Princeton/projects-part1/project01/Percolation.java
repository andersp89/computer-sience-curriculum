package week1;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import edu.princeton.cs.algs4.StdOut;

public class Percolation {
    private int[][] grid;
    final private WeightedQuickUnionUF wquf;
    private int openSites;
    final private int numberOfRows;
    final private int totalSites;
    
    //initialize variables and n-by-n grid of WeighedQuikcUnionUF
    public Percolation(int n) {
	if (n < 0) throw new IllegalArgumentException();
	this.grid = new int[n][n];
	this.numberOfRows = n;
	this.totalSites = n * n;
	this.openSites = 0;
	//+2, because 1 for virtual top and 1 for virtual bottom
	this.wquf = new WeightedQuickUnionUF(n * n + 2);
    }
    
    //opens the site (row, col) if it is not open already
    public void open(int row, int col) {
	validate(row, col);
	if (!isOpen(row, col)) {
	    int currentKey = key(row, col);
	    
	    //if top row, connect to virtual top (0)
	    if (row == 1) { 
		wquf.union(currentKey, 0);
	    }
	    // if last row, connect to virtual bottom row
	    if (row == numberOfRows) {
		wquf.union(currentKey, totalSites + 1);
	    }
	    
	    // check if left element is open, iff join the two
	    if (col - 1 > 0 && isOpen(row, col - 1)) {
		int leftKey = key(row, col - 1);
		wquf.union(currentKey, leftKey);
	    }

	    // check if bottom element is open, iff join the two
	    if (row + 1 <= numberOfRows && isOpen(row + 1, col)) {
		int downKey = key(row + 1, col);
		wquf.union(currentKey, downKey);
	    }

	    // check if right is open, iff join the two
	    if (col + 1 <= numberOfRows && isOpen(row, col + 1)) {
		int rightKey = key(row, col + 1);
		wquf.union(currentKey, rightKey);
	    }

	    // check if top is open, iff join the two
	    if (row - 1 > 0 && isOpen(row - 1, col)) {
		int upKey = key(row - 1, col);
		wquf.union(currentKey, upKey);
	    }
	    //mark current element as open (-1 as grid[n][n] starts from 0)
	    grid[row-1][col-1] = 1;
	    openSites++;
	}
    }

    //check if element is open
    public boolean isOpen(int row, int col) {
	validate(row, col);
	return (grid[row-1][col-1] == 1) ? true : false;
    }
    
    //convert from format (row,col) to element number wguf 
    private int key(int row, int col) {
	return (numberOfRows * (row - 1)) + col;
    }

    //check if site is full (element is open and connected to top)
    public boolean isFull(int row, int col) {
	validate(row, col);
	int currKey = key(row, col);
	if (isOpen(row, col) && (wquf.find(currKey) == wquf.find(0))) {
	    return true;
	}
	return false;
    }
    
    //return number of opened sites
    public int numberOfOpenSites() {
	return openSites;
    }

    //check if system percolates, i.e. virtual top and bottom are connected.
    public boolean percolates() {
	return wquf.find(0) == wquf.find(totalSites+1);
    }
    
    //check that row and col are within bounds
    private void validate(int row, int col) {
	if (row <= 0) throw new IllegalArgumentException("Row should not be negative & zero");
	if (col <= 0) throw new IllegalArgumentException("Column should not be negative & zero");
	if (row > numberOfRows) throw new IllegalArgumentException("Row should not exceed " + (numberOfRows));
	if (col > numberOfRows) throw new IllegalArgumentException("Row should not exceed " + (numberOfRows));
    }

    //testing
    public static void main(String[] arg) {
	Percolation p = new Percolation(3);
	p.open(1,1);
	StdOut.println("Element perculated: " + p.isFull(1, 1));
	StdOut.println("System perculated?: " + p.percolates());
	p.open(2,1);
	StdOut.println("Element perculated: " + p.isFull(2, 1));
	StdOut.println("System perculated?: " + p.percolates());
	p.open(3,1);
	StdOut.println("Element perculated: " + p.isFull(3, 1));
	StdOut.println("System perculated?: " + p.percolates());
    }
}