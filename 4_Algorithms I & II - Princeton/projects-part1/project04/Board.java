package week4;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

/**
 * Write a program to solve the 8-puzzle problem 
 * (and its natural generalizations) using the A* search algorithm.
 * 
 * The problem. 
 * The 8-puzzle is a sliding puzzle that is played on 
 * a 3-by-3 grid with 8 square tiles labeled 1 through 8, plus a 
 * blank square. The goal is to rearrange the tiles so that 
 * they are in row-major order, using as few moves as possible. 
 * 
 * You are permitted to slide tiles either horizontally or 
 * vertically into the blank square. The following diagram 
 * shows a sequence of moves from an initial board (left) 
 * to the goal board (right).
 * 
 * Board data type.
 * To begin, create a data type that 
 * models an n-by-n board with sliding tiles. 
 * Implement an immutable data type Board with the following API.
 * 
 * Constructor:
 * May assume that the constructor receives an n-by-n array containing
 * the n^2 integers between 0 and n^2-1. Where 0 is blank. 2 <= n < 128.
 * 
 * String representation:
 * The toString() method returns a string composed of n+1 lines.
 * First line: board size n.
 * The remaining n lines contains the n-by-n grid of tiles in row-major order,
 * using 0 to designate the blank square.
 * 
 * Hamming and Manhattan distances.  
 * To measure how close a board is to the goal board, we define two notions of distance. 
 * _The Hamming distance betweeen a board and the goal board is 
 * the number of tiles in the wrong position. 
 * _The Manhattan distance between a board and the goal board is
 * the sum of the Manhattan distances (sum of the vertical and 
 * horizontal distance) from the tiles to their goal positions.
 * 
 * Comparing two boards for eqaulity 
 * Two boards are equal if they are have the same size and their corresponding 
 * tiles are in the same positions. The equals() method is inherited from 
 * java.lang.Object, so it must obey all of Java’s requirements.
 * 
 * Neighboring boards
 * The neighbors() method returns an iterable containing the neighbors of the board. 
 * Depending on the location of the blank square, a board can have 2, 3, or 4 neighbors.
 * 
 * Unit testing
 * main() method test public methods
 * 
 * Performance requirements: all boards methods in n^2
 */

public class Board {
    private final int[][] board;
    private final int bLength;
    private int manhattan = -1; //cache
    // coordinates for blank:
    private int zrow;
    private int zcol;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
	if (tiles == null) throw new IllegalArgumentException();
	//constructor receives an n-by-n array containing
	//the n^2 integers between 0 and n^2-1. Where 0 is blank. 2 <= n < 128.	
	this.bLength = tiles.length;
	board = new int[bLength][bLength];
	
        for (int i = 0; i < bLength; i++)
            for (int j = 0; j < bLength; j++) {
                board[i][j] = tiles[i][j];

                if (board[i][j] == 0) {
                    zrow = i;
                    zcol = j;
                }
            }
    }

    // string representation of this board
    public String toString() {
	StringBuilder boardAsString = new StringBuilder();
	boardAsString.append(bLength + "\n");
	for (int i = 0; i < bLength; i++) {
	    for (int j = 0; j < bLength; j++) {
		boardAsString.append(String.format("%2d ", board[i][j]));	
	    }
	    boardAsString.append("\n");
	}
	return boardAsString.toString();
    }

    // board dimension n
    public int dimension() {
	return bLength;
    }

    // number of tiles out of place
    public int hamming() {
	int hammingCount = 0;
	int nCount = 0;
	for (int i = 0; i < bLength; i++) 
	    for (int j = 0; j < bLength; j++) {
		nCount++;
		if (board[i][j] != nCount && board[i][j] != 0) {
		    hammingCount++;
		}
	    }

	return hammingCount;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
	if (manhattan != -1) {
	    return manhattan;
	} else {
	    manhattan = 0;

	    for (int i = 0; i < bLength; i++)
		for (int j = 0; j < bLength; j++)
		    if (board[i][j] != 0) {
			int tile = board[i][j] - 1;
			manhattan += Math.abs(tile / bLength - i) + Math.abs(tile % bLength - j);
		    }
	    return manhattan;
	}
    }

    // is this board the goal board?
    public boolean isGoal() {
	return manhattan() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null)
            return false;

        if (y == this)
            return true;

        if (y.getClass() != getClass())
            return false;

        Board that = (Board) y;

        if (that.dimension() != dimension())
            return false;

        for (int i = 0; i < bLength; i++)
            for (int j = 0; j < bLength; j++)
                if (board[i][j] != that.board[i][j])
                    return false;
        return true;
    }

    // a board that is obtained by exchanging 2 elements in the same row
    // blank should not be exchanged
    public Board twin() {
        Board twin = new Board(board);
        // set row to position of blank + 1, as:
        // if blank in row 1 with size 3, then: (1+1)%3 = 2 
        // (remainder that is not divisible by 3)
        int row = (twin.zrow + 1) % bLength;
        //save in tmp
        int tmp = twin.board[row][0];
        //overwrite position
        twin.board[row][0] = twin.board[row][1];
        //and switch
        twin.board[row][1] = tmp;

        return twin;
    }
    

    // all neighboring boards
    public Iterable<Board> neighbors() {
	// return iterable with neighbor boards
	// if blank in corner 2 neighbors
	// if blank in side 3 neighbors
	// if blank in between 4 neighbors
	int[] offsets = {-1, 1, 0, 0};
        Queue<Board> neighbors = new Queue<Board>();
        
        for (int i = 0, j = offsets.length - 1; i < offsets.length; i++, j--) {
            // initializes, 1) zrow-1, 2) zrow+1, 3) zcol+1, 4) zcol-1 
            int row = zrow + offsets[i];
            int col = zcol + offsets[j];
            
            // if blank is: top || bottom || left || col
            if (row < 0 || row >= bLength || col < 0 || col >= bLength)
                //then skip
        	continue;
            
            // make neighbor
            Board neighbor = new Board(board);
            neighbor.board[zrow][zcol] = neighbor.board[row][col];
            neighbor.board[row][col] = 0;
            //set blank position
            neighbor.zrow = row;
            neighbor.zcol = col;
            neighbors.enqueue(neighbor);
        }

        return neighbors;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
	// create initial board from file
	In in = new In(args[0]);
	int n = in.readInt();
	int[][] tiles = new int[n][n];
	for (int i = 0; i < n; i++)
	    for (int j = 0; j < n; j++)
		tiles[i][j] = in.readInt();
	Board board = new Board(tiles);

	// try toString
	StdOut.println(board);

	//dimension
	StdOut.println("Board dimension: " + board.dimension());

	//hamming
	StdOut.println("Hamming count: " + board.hamming());

	//manhatten
	StdOut.println("Manhatten count: " + board.manhattan());

	//isGoal
	StdOut.println("Is done?: " + board.isGoal());

	//equal
	StdOut.println("Equal to another board?: " + board.equals(board));

	//neighboring boards
	StdOut.println("Neighboring boards:");
	Iterable<Board> result = board.neighbors();
	for (Board b : result) {
	    StdOut.println(b.toString());
	}

	//twin
	StdOut.println("Twin:");
	StdOut.println(board.twin());
    }
}