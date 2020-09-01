package h4_Battleship;
/**
 * This describes characteristics common to all the ships. 
 * @author anderspedersen
 *
 */
public abstract class Ship {
    //instance vars
    private int bowRow; //row 0-19 which contains the bow (front) of the ship
    private int bowColumn; //column which contains the bow (front) of the ship
    private int length; //number of squares occupied by the ship. An "empty sea" location has length 1.
    private boolean horizontal; //true if the ship occupies a single row, false otherwise. Ships will either be placed vertically or horizontally
    private boolean[] hit; //boolean array of size 8 that record hits. Only battleships use all the locations. The other ship will use fewer.

    public int getBowRow() {
	return bowRow;
    }
    public void setBowRow(int bowRow) {
	this.bowRow = bowRow;
    }

    public int getBowColumn() {
	return bowColumn;
    }
    public void setBowColumn(int bowColumn) {
	this.bowColumn = bowColumn;
    }

    public int getLength() {
	return length;
    }
    public void setLength(int length) {
	this.length = length;
    }

    public boolean isHorizontal() {
	return horizontal;
    }
    public void setHorizontal(boolean horizontal) {
	this.horizontal = horizontal;
    }

    public boolean[] getHit() {
	return hit;
    }
    public void setHit(boolean[] hit) {
	this.hit = hit;
    }

    //abstract method therefore no body, must be implemented by other
    public abstract String getShipType();

    /**
     * returns true if it is ok to put a ship of this length with its bow in this location, 
     * with the given orientation, returns false otherwise. A ship cannot:
     * 		overlap another,
     * 		or touch another ship (vert., horiz., diagon.), 
     * 		and must not stick out beyond the array. 
     */
    public boolean okToPlaceShipAt(int row, int column, boolean horizontal, Ocean ocean) {
	if (horizontal) {
	    //check if ship fits ocean
	    if (column + getLength() > ocean.getShipArray()[0].length) {
		return false; 
	    }
	    //try all combinations of x +/- 1 in both row and col for the boats length, to try all adjacent coordinates
	    //catch exception if adjacent coordinates are out of bound
	    for (int i = row - 1; i <= row + 1; i++) {
		for (int j = column - 1; j < column + getLength() + 1; j++) {
		    try {	
			if (!ocean.getShipArray()[i][j].getShipType().equals("empty")) {
			    return false;
			}
		    } catch (Exception e) {
			continue;
		    }
		}
	    }	    
	} else if (!horizontal) {
	    //check if ship fits ocean
	    if (row + getLength() > ocean.getShipArray().length) {
		return false;
	    }
	    //try all combinations of x +/- 1 in both row and col for the boats length, to try all adjacent coordinates
	    //catch exception if adjacent coordinates are out of bound
	    for (int i = row - 1; i < row + getLength() + 1; i++) {
		for (int j = column - 1; j <= column + 1; j++) {
		    try {
			if (!ocean.getShipArray()[i][j].getShipType().equals("empty")) { 
			    return false;
			}
		    } catch (Exception e) {
			continue;
		    }
		}
	    }
	}
	return true;
    }

    /**
     * puts the ship in the ocean
     * giving values to instance variables of ship: bowRow, bowColumn, and horizontal
     * putting a reference to the ship in each of 1 or more locations (up to 8) in the ships array in the ocean object
     */
    public void placeShipAt(int row, int column, boolean horizontal, Ocean ocean) {
	this.bowRow = row;
	this.bowColumn = column;
	this.horizontal = horizontal;
	if (horizontal) {
	    for (int j = column; j < column+getLength(); j++) {
		ocean.getShipArray()[row][j] = this;
	    }    
	} else if (!horizontal) {
	    for (int i = row; i < row+getLength(); i++) {
		ocean.getShipArray()[i][column] = this;
	    }
	}
    }


    /**
     * If a part of the ship occupies the given row and column, and the ship hasn’t been sunk, 
     * mark that part of the ship as ”hit” (in the hit array, 0 indicates the bow) and return true, 
     * otherwise return false.
     */
    public boolean shootAt(int row, int column) {
	if (!this.isSunk()) {
	    if (horizontal) {
		hit[column - this.bowColumn] = true;
		return true;
	    } else if (!horizontal) {
		hit[row - this.bowRow] = true;
		return true;
	    }
	}
	return false;
    }

    /**
     * return true if every part of the ship has been hit, false otherwise
     */
    public boolean isSunk() {
	for (int i = 0; i < this.getLength(); i++) {
	    if (this.getHit()[i] == false) {
		return false;
	    }
	}
	return true;
    }

    /**
     * return a single-char string to use in the Ocean's print method
     * "S" not sunken ship (fired upon and hit a real ship) 
     * "x" sunken ship
     */
    @Override
    public String toString() {
	if (this.isSunk()) {
	    return "x";
	} else {
	    return "S";
	}
    }
}
