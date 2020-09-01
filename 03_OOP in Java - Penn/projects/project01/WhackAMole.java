package h1_WhackAMole;
import java.util.*;

public class WhackAMole {
    int score;
    int molesLeft;
    int attemptsLeft;
    char[][] moleGrid;

    /** 
     * Initializing variables and molegrid with the * character
     *  
     * @param numAttempts the total number of whacks allowed
     * @param gridDimension dimension of grid, width and height
     */
    public WhackAMole (int numAttempts, int gridDimension) {
	this.score = 0;
	this.molesLeft = 0;
	this.attemptsLeft = numAttempts;
	this.moleGrid = new char[gridDimension][gridDimension];

	for (int i = 0; i < moleGrid.length; i++) {
	    for (int j = 0; j < moleGrid[0].length; j++) {
		moleGrid[i][j] = '*';
	    }
	}
    }

    /**
     * given a location, place a mole at that location. Return true if you can.
     * Also update number of moles left.
     */
    public boolean place (int x, int y) {
	if (moleGrid[x][y] == 'M') return false;
	moleGrid[x][y] = 'M';
	molesLeft += 1;
	return true;
    }

    /**
     * Given a location, take a whack at that location. If that location contains 
     * a mole, the score, number of attemptsLeft, and molesLeft is updated. 
     * If that location does not contain a mole, only attemptsLeft is updated.
     */
    public void whack(int x, int y) {
	if (moleGrid[x][y] == 'M') {
	    score += 1;
	    molesLeft--;
	    moleGrid[x][y] = 'W';
	}
	attemptsLeft--;
    }

    /**
     * Print the grid without showing where the moles are. For every spot that 
     * has recorded a "whacked mole", print out a W, or * otherwise.
     */
    public void printGridToUser() {
	for (int i = 0; i < moleGrid.length; i++) {
	    for (int j = 0; j < moleGrid[0].length; j++) {
		if (moleGrid[i][j] == 'W') {
		    System.out.print("W");
		} else {
		    System.out.print("*");
		}
		// New line every 10th char
		if (j == moleGrid[0].length-1) {
		    System.out.println("");
		}
	    }
	}
    }

    /**
     * Print the grid completely. This is effectively dumping the 2d array 
     * on the screen. The places where the moles are should be printed as an 'M'.
     * The places where the moles have been whacked should be printed as a 'W'. 
     * The places that don't have a mole should be printed as *.
     */ 
    public void printGrid() {
	for (int i = 0; i < moleGrid.length; i++) {
	    for (int j = 0; j < moleGrid[0].length; j++) {
		if (moleGrid[i][j] == 'W') {
		    System.out.print("W");
		} else if (moleGrid[i][j] == 'M'){
		    System.out.print("M");
		} else {
		    System.out.print("*");
		}
		// New line every 10th char
		if (j == moleGrid[0].length-1) {
		    System.out.println();
		}
	    }
	}
    } 

    /**
     * Create a 10 * 10 grid, place 10 moles randomly. Now allow the user with Scanner to enter the x and
     * y coordinates of their whack tell them, they have a maximum of 50 attempts to get all the moles.
     * At any point in the game, they can input -1, -1, in order to indicate they are giving up. 
     * If the user gives up, they get to see the entire grid.
     * Game ends if the user is able to uncover all the moles or if the user runs out of attempts.
     */
    public static void main(String[] args) {
	WhackAMole newGame = new WhackAMole(50, 10);

	// Place 10 moles randomly
	int max = 9;
	int min = 0;
	for (int i = 0; i < 10; i++) {
	    int xRandom = (int )(Math.random() * max + min);
	    int yRandom = (int )(Math.random() * max + min);
	    if(newGame.place(xRandom, yRandom)) {
		continue;
	    } else {
		break;
	    }
	}

	Scanner scanner = new Scanner(System.in);
	System.out.println("You must whack all the moles, by typing in x,y-coordinates. The moles are hidden in the 10x10 matrix below.");
	System.out.println("You have a maximum of 50 attempts to get all the moles! Good luck...");

	while (newGame.attemptsLeft > 0 && newGame.molesLeft > 0) {
	    newGame.printGridToUser();
	    System.out.println("Enter x-coordinate: ");
	    int userXCoordinate = scanner.nextInt();
	    System.out.println("Enter y-coordinate: ");
	    int userYCoordinate = scanner.nextInt();
	    if (userXCoordinate == -1 && userYCoordinate == -1) {
		System.out.println("Sorry to see you go, good bye! Here's the complete grid:");
		newGame.printGrid();
		break;
	    }
	    newGame.whack(userXCoordinate, userYCoordinate);
	}

	scanner.close();
	return;
    }

}
