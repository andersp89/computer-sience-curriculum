package h4_Battleship;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class OceanTest {

    @Test
    public void testConstructor() {
	Ocean ocean = new Ocean();
	assertEquals(0, ocean.getHitCount());
	assertEquals(0, ocean.getHitCount());
	assertEquals(0, ocean.getShotsFired());
	//check if all instances are of class EmptySea
	for (int i = 0; i < 20; i++) {
	    for (int j = 0; j < 20; j++) {
		assertEquals("empty", ocean.getShipArray()[i][j].getShipType());
	    }
	}
    }

    @Test
    void testPlaceAllShipsRandomly() {
	Ocean ocean = new Ocean();
	ocean.placeAllShipsRandomly();
	assertEquals(0, ocean.getHitCount());
	assertEquals(0, ocean.getShipsSunk());
	assertEquals(0, ocean.getShotsFired());
	int shipCount = 0;
	for (int i = 0; i < 20; i++) {
		for (int j = 0; j < 20; j++) {
			if (!ocean.getShipArray()[i][j].getShipType().equals("empty")) {
				shipCount++;
			}
			
		}
	}
	// 8 + 7 + 2*6 + 2*5 + 3*4 + 4*3 = 61
	assertEquals(61, shipCount);
    }

    @Test
    void testIsOccupied() {
	//test if no ship is handled properly
	Ocean ocean = new Ocean();
	for (int i = 0; i < 20; i++) {
		for (int j = 0; j < 20; j++) {
			assertFalse( ocean.isOccupied(i, j));
		}
	}
    }
    
    @Test 
    void testIsOccupied2() {
	//test if ship is found
	Ocean ocean = new Ocean();
	BattleShip ship = new BattleShip();
	ship.placeShipAt(0, 0, true, ocean);
	for (int j = 0; j < 8; j++) {
		assertTrue( ocean.isOccupied(0, j));
	}
	assertFalse( ocean.isOccupied(0, 8));
    }

    @Test
    void testShootAt() {
	Ocean ocean = new Ocean();
	BattleShip ship = new BattleShip();
	ship.placeShipAt(0, 0, true, ocean);
	ocean.shootAt(0, 0);
	ocean.shootAt(0, 0);
	ocean.shootAt(1, 0);
	assertEquals(2, ocean.getHitCount());
	assertEquals(0, ocean.getShipsSunk());
	assertEquals(3, ocean.getShotsFired());
    }
}
