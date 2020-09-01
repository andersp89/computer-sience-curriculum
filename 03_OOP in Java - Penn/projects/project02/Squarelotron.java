package h2_Squarelotron;
/**
 * Program that will help us flip and rotate squarelotrons.
 * @author anderspedersen
 */

public class Squarelotron {
    int[][] squarelotron;
    int size;

    /**
     * Constructor to initialize instance variables
     * sets the size instance to be n.
     * fill 2-dimensional array with the numbers 1 to n squared, in order. 
     */
    public Squarelotron(int n) {
	this.size = n;
	this.squarelotron = new int[n][n];

	int sizeSquared = 0;
	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		sizeSquared += 1;
		squarelotron[i][j] = sizeSquared; 
	    }
	}
    }

    /**
     * In each of the following methods, the ring should be a number and 
     * we number from the outermost ring being the number 1.
     */

    /**
     * This method performs the Upside-Down Flip of the squarelotron, as described above, 
     * and returns the new squarelotron. The original squarelotron should not be modified 
     * (we will check for this).
     * @param ring number
     * @return object Squarelotron after upside-down flip
     */

    Squarelotron upsideDownFlip(int ring) {
	Squarelotron modifiedSquare = new Squarelotron(this.size);

	/*
	 * Logic behind change in "i":
	 * size=4, i,j:		With ring=1:
	 * 0,0 0,1 0,2 0,3 	i = size-1-i, 4-1-0=3	With ring=2:	
	 * 1,0 1,1 1,2 1,3	i = size-1-i, 4-1-1=2	i = size-i-1, 4-1-1 = 2 men den skulle være 2
	 * 2,0 2,1 2,2 2,3	i = size-1-i, 4-1-2=1	i = size-i-1, 4-2-2 = 0, men skulle være 1
	 * 3,0 3,1 3,2 3,3	i = size-1-i, 4-1-3=0
	 * 
	 * Change only the coordinates needed by building the right if-statements
	 */

	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		// If ring>1, e.g. 2, then out of bound of array (-1), hence this if: 
		if (i >= ring-1 && i <= size-ring) { 
		    // If first or last rows, change all numbers that are within the ring
		    if ((i == ring-1 || i == size-ring) && (j >= ring-1 && j <= size-ring)) { //)) { 
			modifiedSquare.squarelotron[i][j] = this.squarelotron[size-i-1][j];
		    } 
		    // If between first and last row, then copy only those numbers that are located on the ring
		    else if (j == ring-1 || j == size-ring) {// if (j == ring-1 || j == size-ring) {
			modifiedSquare.squarelotron[i][j] = this.squarelotron[size-i-1][j];
		    }
		}
	    }
	}
	
	return modifiedSquare;


    }

    /**
     * This method performs the Main Diagonal Flip of the squarelotron, as described 
     * above, and returns the new squarelotron. The original squarelotron should not 
     * be modified (we will check for this).
     * @param ring number
     * @return object Squarelotron after diagonal flip
     */

    Squarelotron mainDiagonalFlip(int ring) {
	// new square to apply main diagonal flip at
	Squarelotron modifiedSquare = new Squarelotron(this.size);

	/*
	 * Logic behind change in "i":
	 * size=4, i,j:		With ring=x:
	 * 0,0 0,1 0,2 0,3 	i = j, j = i	
	 * 1,0 1,1 1,2 1,3	i = j, j = i
	 * 2,0 2,1 2,2 2,3	i = j, j = i
	 * 3,0 3,1 3,2 3,3	i = j, j = i
	 * 
	 * Change only the coordinates needed by building the right if-statements
	 */

	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		// If ring>1, then out of bound of array (-1), if not this if: 
		if (i >= ring-1 && i <= size-ring) { 
		    // If first or last rows, change all numbers that are within the ring
		    if ((i == ring-1 || i == size-ring) && (j >= ring-1 && j <= size-ring)) { 
			modifiedSquare.squarelotron[i][j] = this.squarelotron[j][i];
		    } 
		    // If between first and last row, then copy only those numbers that are located on the ring
		    else if (j == ring-1 || j == size-ring) {
			modifiedSquare.squarelotron[i][j] = this.squarelotron[j][i];
		    }
		}
	    }
	}
	return modifiedSquare;
    }

    /**
     * The argument numberOfTurns indicates the number of times the entire squarelotron 
     * should be rotated 90° clockwise. Any integer, including zero and negative integers, 
     * is allowable as the argument. A value of -1 indicates a 90° counterclockwise rotation. 
     * This method modifies the internal representation of the squarelotron; it does not 
     * create a new squarelotron.
     * @param numberOfTurns, if > 0 then clockwise, if < 0 then counterclockwise.
     */

    public void rotateRight(int numberOfTurns) {
	// absolute value, to take care of negative numbers
	numberOfTurns = Math.abs(numberOfTurns);
	for (int n = 0; n < numberOfTurns; n++) {
	    Squarelotron newSquare = new Squarelotron(size);
	    for (int i = 0; i < size; i++) {
		for (int j = 0; j < size; j++) {
		    newSquare.squarelotron[j][size-1-i] = squarelotron[i][j]; 
		}
	    }
	    this.squarelotron = newSquare.squarelotron;
	}
    }
    
    /**
     * Function, not part of exercise, to print 2d array
     */
    
    public void printIt() {
	for (int i = 0; i < squarelotron.length; i++) {
	    for (int j = 0; j < squarelotron[0].length; j++) {
		System.out.print(squarelotron[i][j]+" ");
		// New line every n'th int
		if (j == squarelotron[0].length-1) {
		    System.out.println("");
		}
	    }
	}
    }

    public static void main(String[] args) {
	Squarelotron square = new Squarelotron(3);
	square.rotateRight(1);
	square.printIt();
	
    }

}
