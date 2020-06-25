package h4_Battleship;
import java.util.Random;

/**
 * This contains a 20x20 array of Ships, representing the ”ocean,” 
 * and some methods to manipulate it.
 * @author anderspedersen
 *
 */
public class Ocean {
    //should they be private?
    private Ship[][] ships = new Ship[20][20]; //grid to sea
    private int shotsFired; //total number of shots fired by the user
    private int hitCount; //number of times a shot hit a ship. ALso, if user hits same place more than once
    private int shipsSunk; //number of ships sunk. In total 13.

    //constructor
    public Ocean() {
	//creates an "empty" ocean, fills the ships array with a bunch of EmptySea instances.
	for (int i = 0; i < this.ships.length; i++) {
	    for (int j = 0; j < this.ships[0].length; j++) {
		EmptySea emptySea = new EmptySea();
		emptySea.placeShipAt(i, j, true, this);
	    }
	}
	//initializing game variables
	this.shotsFired = 0;
	this.hitCount = 0;
	this.shipsSunk = 0;	
    }

    //methods

    /**
     * Place all ships randomly on the initially empty ocean. Place larger before smaller ones.
     * Use random class in java.util package.
     * Ship quantity and type:
     * one 8-square Battleship, 
     * one 7-square Battlecruiser, 
     * two 6-square Cruisers, 
     * two 5-square Light Cruisers, 
     * three 4-square Destroyers and
     * four 3-square Submarines. 
     */
    public void placeAllShipsRandomly() {
	Ship[] ships = new Ship[13];
	for (int i = 0; i < 13; i++) {
	    if (i == 0) {
		ships[i] = new BattleShip();
	    } else if (i == 1) {
		ships[i] = new BattleCruiser();
	    } else if (i < 4) {
		ships[i] = new Cruiser();
	    } else if (i < 6) {
		ships[i] = new LightCruiser();
	    } else if (i < 9) {
		ships[i] = new Destroyer();
	    } else if (i < 13) {
		ships[i] = new Submarine();
	    }
	}

	Random random = new Random();
	for (Ship ship : ships) {
	    while (true) {
		int row = random.nextInt(20);
		int column = random.nextInt(20);
		boolean horizontal = random.nextBoolean();
		if (ship.okToPlaceShipAt(row, column, horizontal, this)) {
		    ship.placeShipAt(row, column, horizontal, this);
		    break;
		}
	    }
	}
    }

    /**
     * returns true if the given location contains a ship, false if not.
     */
    public boolean isOccupied(int row, int column) {
	if (!this.getShipArray()[row][column].getShipType().equals("empty")) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Returns true if the given location contains a ”real” ship, still afloat, (not an EmptySea), false if it does not. 
     * In addition, this method updates the number of shots that have been fired, and the number of hits. 
     * Note: If a location contains a ”real” ship, shootAt should return true every time the user shoots at that same location. 
     * Once a ship has been ”sunk”, additional shots at its location should return false.
     */
    public boolean shootAt(int row, int column) {
	this.shotsFired++;
	if (isOccupied(row, column)) {	
	    //shootAt is true, only if isSunk is false, hence isSunk will be called only once
	    if (ships[row][column].shootAt(row, column)) {
		if (ships[row][column].isSunk()) {
		    System.out.println("You just sunk a " + ships[row][column].getShipType());
		    this.shipsSunk++;
		}
		this.hitCount++;
		return true;
	    }
	    return false;
	} else {
	    //to print "-"
	    ships[row][column].shootAt(row, column);
	    return false;
	}
    }

    /**
     * returns the number of shots fired in this game
     */
    public int getShotsFired() {
	return shotsFired;
    }

    /**
     * returns the number of hits recorded in this game. All hits counted, not just the first time a given square is hit
     */
    public int getHitCount() {
	return hitCount;
    }

    /**
     * return number of ships sunk
     */
    public int getShipsSunk() {
	return shipsSunk;
    }

    /**
     * return true if all ships have been sunk
     */
    public boolean isGameOver() {
	if (shipsSunk == 13) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * returns the 20x20 array of ships.
     * methods of other classes are going to modify array. 
     * While it is undesirable to do so, sometimes there is no good alternative
     */
    public Ship[][] getShipArray() {
	return ships;
    }

    /**
     * print the ocean
     * row numbers from 00 till 19 along left edge of array
     * column numbers from 00 till 19 along the top
     * 
     * "S" fired upon and hit a real ship.
     * "-" fired upon and found nothing. <-- Needs to be done here!
     * "x" sunken ship
     * "." never fired upon
     */
    public void print() {
	int colCounter = 0;
	int rowCounter = 0;
	for (int row = 0; row < ships.length; row++) {
	    for (int column = 0; column < ships[0].length; column++) {
		//print top row, 00 till 19
		if (row == 0 && column == 0) {
		    for (int k = 0; k < 20; k++) {
			//empty space for top-left corner
			if (k == 0) {
			    System.out.print("   ");
			}
			//print "0" for single digit
			if (colCounter < 10) {
			    System.out.print("0" + colCounter + " ");
			} else {
			    System.out.print(colCounter + " ");
			}
			colCounter += 1;
			//print new line when top row done
			if (k == 19) {
			    System.out.println();
			}
		    }
		}

		//print left col, 00 till 19
		if (column == 0) {
		    //print "0" for single digit
		    if (rowCounter < 10) {
			System.out.print("0" + rowCounter + " ");
		    } else {
			System.out.print(rowCounter + " ");
		    }
		    rowCounter += 1;
		}

		//show ship, only if boat is hit by user already
		if (!ships[row][column].getShipType().equals("empty")) {
		    if (ships[row][column].isHorizontal()) {
			if (ships[row][column].getHit()[column - ships[row][column].getBowColumn()] == true) { //|| ships[i][j].isSunk()) {
			    System.out.print(ships[row][column] + " ");
			} else {
			    System.out.print(". ");
			}
		    } else if (!ships[row][column].isHorizontal()) {
			if (ships[row][column].getHit()[row - ships[row][column].getBowRow()] == true) { //|| ships[i][j].isSunk()) {
			    System.out.print(ships[row][column] + " ");
			} else {
			    System.out.print(". ");
			}
		    } 
		} else {
		    System.out.print(ships[row][column] + " ");
		}

		//print new line at every 20th char
		if (column == ships.length-1) {
		    System.out.println();
		}
	    }

	}	    
    }
}
