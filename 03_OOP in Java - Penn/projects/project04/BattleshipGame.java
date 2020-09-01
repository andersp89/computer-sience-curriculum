package h4_Battleship;
import java.util.Scanner;

/**
 *  Game: Battleship
 *  Battleship is usually a two-player game, where each player has a fleet and an ocean (hidden from the other player), 
 *  and tries to be the first to sink the other player’s fleet. We will do just a solo version, where the computer places the ships, 
 *  and the human attempts to sink them.
 *  
 *  We’ll play this game on a 20x20 ocean. This is larger than the ocean in the traditional battleship game.  In this game we will 
 *  have one 8-square Battleship, one 7-square Battlecruiser, two 6-square Cruisers, two 5-square Light Cruisers, 
 *  three 4-square Destroyers and four 3-square Submarines. Finally, unlike the traditional game, 
 *  A player can shoot 5 times in each turn.
 *  
 *  The computer places these 13 ships on the ocean in such a way that no ships are immediately adjacent to each other, 
 *  either horizontally, vertically, or diagonally.
 *  
 *  The human player does not know where the ships are. The initial display of the ocean shows a 20 by 20 array of locations, all the same.
 *  
 *  The human player tries to hit the ships, by calling out a row and column number. The computer responds with one bit of information 
 *  saying ”hit” or ”miss.” When a ship is hit but not sunk, the program does not provide any information about what kind of a ship was hit. 
 *  However, when a ship is hit and sinks, the program prints out a message ”You just sank a ship-type.” After each shot, 
 *  the computer redisplays the ocean with the new information.
 *  
 *  A ship is ”sunk” when every square of the ship has been hit. Thus, it takes 8 hits to sink a battleship but only 6 to sink a cruiser.
 *  The objective is to sink the fleet with as few shots as possible.
 *  
 *  When all ships have been sunk, the program prints out a message that the game is over, and tells the user how many shots were required.
 *  
 *  This is the ”main” class, containing the main method with all gaming logic and an instance variable of type Ocean.
 * @author anderspedersen
 *
 */
public class BattleshipGame {

    //instance variable
    private Ocean ocean;

    //constructor
    public BattleshipGame() {
	this.ocean = new Ocean();
    }

    public Ocean getOcean() {
	return ocean;
    }

    public void setOcean(Ocean ocean) {
	this.ocean = ocean;
    }
    
    /**
     * In this class you will set up the game; accept ”shots” from the user; display the results; 
     * print final scores; and ask the user if he/she wants to play again. 
     * All input/output is done here (although some of it is done by calling a print() method in the Ocean class.) 
     * All computation will be done in the Ocean class and the various Ship classes.
     */
    public static void main(String[] args) {
	System.out.println("Welcome to the Battle Ship game!");
	BattleshipGame game = new BattleshipGame();
	game.ocean.placeAllShipsRandomly();

	while (true) {
	    //print ocean
	    game.ocean.print();

	    //accepts shots from user
	    System.out.println("Where do you want to hit? The input format should look like this: '1,1; 0,3; 7,3; 9,11; 12,17'. Enter 'q' to quit.");
	    Scanner scanner = new Scanner(System.in);
	    String input = scanner.nextLine();

	    if(input.equals("q")) {
		System.out.println("Sorry to see you go, farewell!");
		break;
	    }

	    String[] userCoordinates = input.split("; ");
	    for (String c : userCoordinates) {
		String[] coordinateRowCol = c.split(",");
		if (game.ocean.shootAt(Integer.parseInt(coordinateRowCol[0]), Integer.parseInt(coordinateRowCol[1]))) {
		    System.out.println("hit");
		} else {
		    System.out.println("miss");
		}
	    }

	    //display game statistics
	    System.out.println();
	    System.out.println("Your game stats:");
	    System.out.println("Shots fired: " + game.ocean.getShotsFired());
	    System.out.println("Shots hit ship: " + game.ocean.getHitCount());
	    System.out.println("Ships sunk: " + game.ocean.getShipsSunk());
	    System.out.println();

	    if (game.ocean.isGameOver()) {
		System.out.println("Yay - you made it. You have hit all ships - game is completed. Play again? yes or no");
		input = scanner.nextLine();
		if (input.equals("yes")) {
		    System.out.println("Welcome to the Battle Ship game!");
		    game = new BattleshipGame();
		    game.ocean.placeAllShipsRandomly();
		}
		System.out.println("Shots fired in game: " + game.ocean.getShotsFired());
		scanner.close();
		break;
	    }

	    System.out.println("Try again:");
	}
    }
}
