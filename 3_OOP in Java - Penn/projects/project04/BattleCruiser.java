package h4_Battleship;
import java.util.Arrays;

/**
 * Describes a battlecruiser - a ship that occupies 7 squares.
 * @author anderspedersen
 *
 */
public class BattleCruiser extends Ship {
    public BattleCruiser() {
	this.setLength(7);
	this.setHit(new boolean[7]);
	Arrays.fill(this.getHit(), false);
    }
    
    @Override
    public String getShipType() {
	return "battlecruiser"; 
    }
}
