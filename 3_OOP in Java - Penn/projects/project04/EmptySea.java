package h4_Battleship;
import java.util.Arrays;

/**
 * Describes a part of the ocean that does not have a ship in it. 
 * While it might seem silly to have the lack of a ship be a type of ship, 
 * this trick does simplify a number of things. 
 * @author anderspedersen
 *
 */
public class EmptySea extends Ship {
    public EmptySea() {
	this.setLength(1);
	this.setHit(new boolean[1]);
	Arrays.fill(this.getHit(),  false);
    }

    /**
     * always return false to indicate that nothing was hit
     */
    @Override
    public boolean shootAt(int row, int column) {
	//set to true, to print "-"
	this.getHit()[0] = true;
	return false;
    }

    /**
     * always return false to indicate that you didn't sink anything
     */
    @Override
    public boolean isSunk() {
	return false;
    }

    /**
     * return a single char String to use in the Ocean's print method
     * you could choose to have an unoccupied sea in many ways
     */
    @Override
    public String toString() {
	return this.getHit()[0] ? "-" : ".";
    }

    /**
     *  returns the string empty
     */
    @Override
    public String getShipType() {
	return "empty";
    }
}
