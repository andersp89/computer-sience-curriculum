package h4_Battleship;
import java.util.Arrays;

/**
 * Describes a battleship - a ship that occupies 8 squares.
 * @author anderspedersen
 */
public class BattleShip extends Ship {
    public BattleShip() {
	this.setLength(8);
	this.setHit(new boolean[8]);
	Arrays.fill(this.getHit(), false);
    }
    
    @Override
    public String getShipType() {
	return "battleship"; 
    }
    
    /* for test only
    @Override
    public String toString() {
	return "s"; 
    }
    */
}
