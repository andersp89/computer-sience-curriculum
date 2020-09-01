package h2_Squarelotron;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SquarelotronTest {
    // used for test
    Squarelotron newSquare;
    Squarelotron modifiedSquare;
    
    @BeforeEach
    void setUp() throws Exception {
	newSquare = new Squarelotron(4);
    }

    @Test
    void testUpsideDownFlip() {
	modifiedSquare = newSquare.upsideDownFlip(1);
	assertEquals(modifiedSquare.squarelotron[0][3], 16);
	modifiedSquare = newSquare.upsideDownFlip(2);
	assertEquals(modifiedSquare.squarelotron[1][2], 11);
    }

    @Test
    void testMainDiagonalFlip() {
	modifiedSquare = newSquare.mainDiagonalFlip(1);
	assertEquals(modifiedSquare.squarelotron[0][3], 13);
    }
    
    @Test
    void testRotateRight() {
	newSquare.rotateRight(3);
	assertEquals(newSquare.squarelotron[3][0], 1);
    }

}
